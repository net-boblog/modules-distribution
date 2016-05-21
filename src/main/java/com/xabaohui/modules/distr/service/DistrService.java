package com.xabaohui.modules.distr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.xabaohui.modules.distr.bean.DistrBatch;
import com.xabaohui.modules.distr.bean.DistrChange;
import com.xabaohui.modules.distr.bean.DistrChangeType;
import com.xabaohui.modules.distr.bean.DistrOrder;
import com.xabaohui.modules.distr.bean.DistrOrderDetail;
import com.xabaohui.modules.distr.bean.DistrOrderRelation;
import com.xabaohui.modules.distr.bean.DistrOrderStatus;
import com.xabaohui.modules.distr.bean.ExpressStatus;
import com.xabaohui.modules.distr.dao.DistrBatchDao;
import com.xabaohui.modules.distr.dao.DistrChangeDao;
import com.xabaohui.modules.distr.dao.DistrOrderDao;
import com.xabaohui.modules.distr.dao.DistrOrderDetailDao;
import com.xabaohui.modules.distr.dao.DistrOrderRelationDao;
import com.xabaohui.modules.distr.dto.CheckResultDTO;
import com.xabaohui.modules.distr.dto.DistrBatchOutOfStockRequestDTO;
import com.xabaohui.modules.distr.dto.DistrBatchOutOfStockRequestDetail;
import com.xabaohui.modules.distr.dto.DistrBatchOutOfStockResponseDTO;
import com.xabaohui.modules.distr.dto.DistrBatchOutOfStockResponseDetail;
import com.xabaohui.modules.distr.dto.DistrBatchPrintDTO;
import com.xabaohui.modules.distr.dto.DistrBatchRequestDTO;
import com.xabaohui.modules.distr.dto.DistrChangeDTO;
import com.xabaohui.modules.distr.dto.DistrExpressDTO;
import com.xabaohui.modules.distr.dto.DistrExpressDetailDTO;
import com.xabaohui.modules.distr.dto.DistrExpressPreDTO;
import com.xabaohui.modules.distr.dto.DistrGoodsDTO;
import com.xabaohui.modules.distr.dto.DistrMapDTO;
import com.xabaohui.modules.distr.dto.DistrOrderAddrDTO;
import com.xabaohui.modules.distr.dto.DistrOrderDTO;
import com.xabaohui.modules.distr.dto.DistrOrderDetailDTO;
import com.xabaohui.modules.distr.util.DateFormat;

public class DistrService {

	private DistrBatchDao distrBatchDao;
	private DistrOrderDao distrOrderDao;
	private DistrOrderDetailDao distrOrderDetailDao;
	private DistrOrderRelationDao distrOrderRelationDao;
	private DistrChangeDao distrChangeDao;

	private Logger log = LoggerFactory.getLogger(DistrService.class);
	
	/**
	 * 订单变更通知
	 * 
	 * @param distrChangeDTO
	 * @return distrOrderId
	 */
	//检查传入的交易订单id
	private void checkTradeOrderId(Integer tradeOrderId){
		if (tradeOrderId == null){
			throw new RuntimeException("传入的交易订单id为空");
		}
	}
	
	//通过交易订单id得到配发系统订单对象
	private DistrOrder findDistrOrderByTradeOrderId(Integer tradeOrderId){
		DistrOrderRelation relation = new DistrOrderRelation();
		relation.setTradeOrderId(tradeOrderId);
		List<DistrOrderRelation> relationList = distrOrderRelationDao.findByExample(relation);
		if (relationList == null || relationList.isEmpty()){
			log.warn("交易id为{}关联的订单id不存在",tradeOrderId);
			throw new RuntimeException("交易id为"+tradeOrderId+"关联的订单id不存在");
		}
		if (relationList.size()>1){
			log.warn("交易id为{}关联的订单id有多条记录",tradeOrderId);
			throw new RuntimeException("交易id为"+tradeOrderId+"关联的订单id有多条记录");
		}
		Integer distrOrderId =  relationList.get(0).getDistrOrderId();
		return distrOrderDao.findById(distrOrderId);
	}
	
	private void assertDistrOrderNotSend(DistrOrder distrOrder) {
		if (distrOrder == null) {
			log.warn("系统订单配发订单不存在");
			throw new RuntimeException("系统订单配发订单不存在");
		}
		if (DistrOrderStatus.EXPORTING_DISTRIBUTION.equals(distrOrder
				.getDistrStatus())) {
			log.warn("订单id为{}的配发系统订单已发货，无法拦截该订单", distrOrder.getDistrOrderId());
			throw new RuntimeException("该订单已发货，无法拦截该订单");
		}
	}
	
	private void checkAddrDTO(DistrOrderAddrDTO addrDTO){
		if (addrDTO == null){
			log.warn("传入的地址DTO为空");
			throw new RuntimeException("传入的地址DTO为空");
		}
		if (StringUtils.isBlank(addrDTO.getReceiveAddr())){
			log.warn("传入的收件人地址为空");
			throw new RuntimeException("传入的收件人地址为空");
		}
		if (StringUtils.isBlank(addrDTO.getReceiveCode())){
			log.warn("传入的收件人邮编为空");
			throw new RuntimeException("传入的收件人邮编为空");
		}
		if (StringUtils.isBlank(addrDTO.getReceiveName())){
			log.warn("传入的收件人姓名为空");
			throw new RuntimeException("传入的收件人姓名为空");
		}
		if (StringUtils.isBlank(addrDTO.getReceivePhoneNo())){
			log.warn("传入的收件人手机号为空");
			throw new RuntimeException("传入的收件人手机号为空");
		}
		if (addrDTO.getReceiveCityId() == null){
			log.warn("传入的城市id为空");
			throw new RuntimeException("传入的城市id为空");
		}
	}
	
	/**
	 * 地址更改
	 * check
	 * change order address
	 * set order change flag
	 * saveToDistrChange
	 * @param tradeOrderId
	 * @param addrDTO
	 */
	public Integer changeOrderAddr(Integer tradeOrderId, DistrOrderAddrDTO addrDTO) {
		checkTradeOrderId(tradeOrderId);
		checkAddrDTO(addrDTO);
		DistrOrder distrOrder = findDistrOrderByTradeOrderId(tradeOrderId);
		assertDistrOrderNotSend(distrOrder);
		BeanUtils.copyProperties(addrDTO, distrOrder);
		distrOrder.setChangeOrderFlag(true);
		updateDistrOrder(distrOrder);
		// TODO 城市ID应该换成城市名称
		String fmt = "订单id为{}的订单地址修改为收件人姓名为{}，收件地址邮编为{}，收件人手机号为{}，收件城市id为{}，收件人地址为{}";
		String infos = String.format(fmt, addrDTO.getReceiveName(), addrDTO.getReceiveCode(),
				addrDTO.getReceivePhoneNo(), addrDTO.getReceiveCityId(),
				addrDTO.getReceiveAddr());
		log.info(infos);
		Integer changeId = saveToDistrChange(tradeOrderId, infos, DistrChangeType.CHANGE_ADDR);
		log.info("订单id为{}的订单添加到变更记录表中", distrOrder.getDistrOrderId());
		return changeId;
	}
	
	/**
	 * 切换快递公司
	 * check
	 * change order express company
	 * set order change flag
	 * saveToDistrChange
	 * @param tradeOrderId
	 * @param expressCompany
	 */
	public Integer changeOrderExpressCompany(Integer tradeOrderId, String expressCompany) {
		checkTradeOrderId(tradeOrderId);
		DistrOrder distrOrder = findDistrOrderByTradeOrderId(tradeOrderId);
		distrOrder.setChangeOrderFlag(true);
		distrOrder.setExpressCompany(expressCompany);
		updateDistrOrder(distrOrder);
		log.info("订单id为{}的订单状态为已切换快递公司为{}",distrOrder.getDistrOrderId(),expressCompany);
		String detail = "快递公司修改为：" + expressCompany;
		Integer changeId = saveToDistrChange(tradeOrderId, detail, DistrChangeType.CHANGE_COMPANY);
		log.info("订单id为{}的订单添加到变更记录表中",distrOrder.getDistrOrderId());
		return changeId;
	}
	
	/**
	 * 拦截订单
	 * check
	 * change order status
	 * saveToDistrChange
	 * @param tradeOrderId
	 * @param memo
	 */
	public Integer blockOrder(Integer tradeOrderId, String memo) {
		checkTradeOrderId(tradeOrderId);
		DistrOrder distrOrder = findDistrOrderByTradeOrderId(tradeOrderId);
		distrOrder.setPeblockStatus(distrOrder.getDistrStatus());
		distrOrder.setDistrStatus(DistrOrderStatus.BLOCK_DISTRIBUTION);
		updateDistrOrder(distrOrder);
		log.info("订单id为{}的订单状态为已被拦截",distrOrder.getDistrOrderId());
		String detail = "拦截订单。" + memo;
		Integer changeId = saveToDistrChange(tradeOrderId, detail, DistrChangeType.ORDER_BLOCKED);
		log.info("订单id为{}的订单添加到变更记录表中",distrOrder.getDistrOrderId());
		return changeId;
	}
	
	/**
	 * 取消拦截订单
	 * check
	 * change order status
	 * saveToDistrChange
	 * @param tradeOrderId
	 * @param memo
	 */
	public Integer unblockOrder(Integer tradeOrderId, String memo) {
		checkTradeOrderId(tradeOrderId);
		DistrOrder distrOrder = findDistrOrderByTradeOrderId(tradeOrderId);
		if (!DistrOrderStatus.BLOCK_DISTRIBUTION.equals(distrOrder
				.getDistrStatus())) {
			log.warn("id为{}的配发系统订单未被拦截,订单状态不是拦截状态", distrOrder.getDistrOrderId());
			throw new RuntimeException("该订单未被拦截");
		}
		distrOrder.setDistrStatus(distrOrder.getPeblockStatus());
		updateDistrOrder(distrOrder);
		log.info("订单id为{}的订单状态为已取消拦截",distrOrder.getDistrOrderId());
		String detail = "恢复已拦截的订单。" + memo;
		Integer distrChangeId = saveToDistrChange(tradeOrderId, detail, DistrChangeType.ORDER_UNBLOCKED);
		log.info("订单id为{}的订单添加到变更记录表中",distrOrder.getDistrOrderId());
		return distrChangeId;
	}
	
	/**
	 * 取消订单
	 * check
	 * cancel order
	 * saveToDistrChange
	 * @param tradeOrderId
	 * @param memo
	 */
	public void cancelOrder(Integer tradeOrderId, String memo) {
		checkTradeOrderId(tradeOrderId);
		DistrOrder distrOrder = findDistrOrderByTradeOrderId(tradeOrderId);
		if (DistrOrderStatus.ORDER_STATUS_CANCEL.equals(distrOrder
				.getDistrStatus())) {
			return;
		}
		distrOrder.setDistrStatus(DistrOrderStatus.ORDER_STATUS_CANCEL);
		updateDistrOrder(distrOrder);
		log.info("订单id为{}的订单状态为已取消",distrOrder.getDistrOrderId());
		String detail = "取消订单，原因为：" + memo;
		saveToDistrChange(distrOrder.getDistrOrderId(), detail, DistrChangeType.ORDER_CANCELED);
		log.info("订单id为{}的订单添加到变更记录表中",distrOrder.getDistrOrderId());
	}

	/**
	 * 订单推送
	 * 
	 * @param distrOrderDTO
	 * @return distrOrderId
	 */
	public Integer saveDistrOrder(DistrOrderDTO distrOrderDTO) {
		// 判断DTO字段是否为空
		checkDistrOrderDTO(distrOrderDTO);
		// 判断是否重复推送订单
		DistrOrderRelation relation = findDistrOrderRelationByTradeOrderId(distrOrderDTO.getTradeOrderId());
		if(relation != null) {
			throw new RuntimeException("重复推送订单，订单ID=" + distrOrderDTO.getTradeOrderId());
		}
		
		// 通过买家id,收件人地址来判断状态
		DistrOrder distrOrder = buildDistrOrderQueryExample(distrOrderDTO);
		distrOrder.setDistrStatus(DistrOrderStatus.WAIT_DISTRIBUTION);
		List<DistrOrder> list = distrOrderDao.findByExample(distrOrder);
		// 若没有wait状态的数据，则直接添加
		if (list == null || list.isEmpty()) {
			Integer distrOrderId = saveToDistrOrderDetailAndRelation(distrOrderDTO);
			log.info("添加系统订单配发表，配发系统关联表和明细表成功，配发系统id为{}", distrOrderId);
			
			// 检查数据库中是否有同地址同买家的订单，如果有，批量标记为已变更，方便在分拣校验和出库阶段进行拦截
			markSameOrderChanged(distrOrderDTO);
			return distrOrderId;
		}
		if (list.size() > 1) {
			log.warn("数据库数据异常，有多条等待配货订单");
			throw new RuntimeException("数据库数据异常，有多条等待配货订单");
		}
		
		// 若存在等待配货的订单，则进行合并
		Integer distrOrderId = list.get(0).getDistrOrderId();
		saveToDistrOrderDetail(distrOrderId, distrOrderDTO);
		log.info("添加明细表成功，其中配发系统订单id为{}", distrOrderId);
		saveToDistrOrderRelation(distrOrderId, distrOrderDTO);
		log.info("添加关联表成功,其中配发系统订单id为{}", distrOrderId);
		
		return distrOrderId;
	}

	private DistrOrderRelation findDistrOrderRelationByTradeOrderId(
			Integer tradeOrderId) {
		DistrOrderRelation relation = new DistrOrderRelation();
		relation.setTradeOrderId(tradeOrderId);
		List<DistrOrderRelation> relationlist = distrOrderRelationDao.findByExample(relation);
		if(relationlist == null || relationlist.isEmpty()) {
			return null;
		}
		if(relationlist.size() > 1) {
			throw new RuntimeException("数据错误：DistrOrderRelation有多条记录，TradeOrderId=" + tradeOrderId);
		}
		return relationlist.get(0);
	}
	
	// 检查数据库中是否有同地址同买家的订单，如果有，批量标记为已变更，方便在分拣校验和出库阶段进行拦截
	private void markSameOrderChanged(DistrOrderDTO distrOrderDTO) {
		DistrOrder example = buildDistrOrderQueryExample(distrOrderDTO);
		List<DistrOrder> list = distrOrderDao.findByExample(example);
		if (list.size()>1){
			for (DistrOrder distrOrder : list) {
				distrOrder.setChangeOrderFlag(true);
				updateDistrOrder(distrOrder);
				log.warn("订单id为{}的订单由于地址重复标记变更", distrOrder.getDistrOrderId());
				String detail = "订单id为" + distrOrder.getDistrOrderId() + "的订单地址有重复";
				saveToDistrChange(distrOrder.getDistrOrderId(), detail,
						DistrChangeType.ORDER_DUPLICATE_ADDR);
				log.info("订单id为{}的订单由于地址重复标记变更添加到变更记录表中",
						distrOrder.getDistrOrderId());
			}
		}
	}

	private DistrOrder buildDistrOrderQueryExample(DistrOrderDTO distrOrderDTO) {
		DistrOrder distrOrder = new DistrOrder();
		distrOrder.setReceiveAddr(distrOrderDTO.getReceiveAddr());
		distrOrder.setReceiveCityId(distrOrderDTO.getReceiveCityId());
		distrOrder.setReceiveCode(distrOrderDTO.getReceiveCode());
		distrOrder.setReceiveName(distrOrderDTO.getReceiveName());
		distrOrder.setReceivePhoneNo(distrOrderDTO.getReceivePhoneNo());
		distrOrder.setReceiverId(distrOrderDTO.getReceiverId());
		return distrOrder;
	}

	// 添加订单变更记录表
	private Integer saveToDistrChange(Integer distrOrderId,String detail,String changeType) {
		DistrChange distrChange = new DistrChange();
		distrChange.setDetail(detail);
		distrChange.setType(changeType);
		distrChange.setDistrOrderId(distrOrderId);
		distrChange.setGmtCreate(DateFormat.getCurrentTime());
		distrChange.setGmtModify(DateFormat.getCurrentTime());
		distrChange.setVersion(1);
		distrChangeDao.save(distrChange);
		log.info("id为{}的配发系统订单id成功添加到变更记录表中", distrOrderId);
		return distrChange.getDistrChangeId();
	}

	private void saveDistrOrder(DistrOrder distrOrder){
		distrOrder.setGmtModify(DateFormat.getCurrentTime());
		distrOrder.setVersion(1);
		distrOrderDao.save(distrOrder);
	}
	// 添加配货系统订单表
	private DistrOrder saveToDistrOrder(DistrOrderDTO distrOrderDTO) {
		checkDistrOrderDTO(distrOrderDTO);
		DistrOrder distrOrder = new DistrOrder();
		BeanUtils.copyProperties(distrOrderDTO, distrOrder);
		distrOrder.setDistrStatus(DistrOrderStatus.WAIT_DISTRIBUTION);
		distrOrder.setExpressStatus(ExpressStatus.WAITPRINT_EXPORT);
		distrOrder.setChangeOrderFlag(false);
		distrOrder.setGmtCreate(DateFormat.getCurrentTime());
		saveDistrOrder(distrOrder);
		log.info("成功添加配货系统订单，配发系统订单id为{}", distrOrder.getDistrOrderId());
		return distrOrder;
	}

	// 添加配货系统订单明细表
	private List<DistrOrderDetail> saveToDistrOrderDetail(Integer distrOrderId,
			DistrOrderDTO distrOrderDTO) {
		checkInteger(distrOrderId);
		checkDistrOrderDTO(distrOrderDTO);
		List<DistrOrderDetail> detailList = new ArrayList<DistrOrderDetail>();
		for (DistrOrderDetailDTO distrOrderDetailDTO : distrOrderDTO.getList()) {
			DistrOrderDetail detail = new DistrOrderDetail();
			detail.setDistrOrderId(distrOrderId);
			BeanUtils.copyProperties(distrOrderDetailDTO, detail);
			detail.setGmtCreate(DateFormat.getCurrentTime());
			detail.setGmtModify(DateFormat.getCurrentTime());
			detail.setVersion(1);
			detailList.add(detail);
			distrOrderDetailDao.save(detail);
			log.info("成功添加配发系统明细表，配发系统订单id为{}", distrOrderId);
		}
		return detailList;
	}

	// 添加订单关联表
	private void saveToDistrOrderRelation(int distrOrderId,DistrOrderDTO distrOrderDTO) {
		checkInteger(distrOrderId);
		checkDistrOrderDTO(distrOrderDTO);
		DistrOrderRelation dor = new DistrOrderRelation();
		dor.setDistrOrderId(distrOrderId);
		dor.setTradeOrderId(distrOrderDTO.getTradeOrderId());
		dor.setGmtCreate(DateFormat.getCurrentTime());
		dor.setGmtModify(DateFormat.getCurrentTime());
		dor.setVersion(1);
		distrOrderRelationDao.save(dor);
		log.info("成功添加到订单关联表，配发系统订单id为{}，交易系统订单id为{}", distrOrderId,
				distrOrderDTO.getTradeOrderId());
	}
	
	//检车传入的int类型的数据
	private void checkInteger(Integer distrOrderId){
		if (distrOrderId == null){
			log.warn("传入的id为空");
			throw new RuntimeException("传入的id为空");
		}
	}
	
	// 验证传过来的dto的值是否为空
	private void checkDistrOrderDTO(DistrOrderDTO distrOrderDTO) {
		if (distrOrderDTO == null){
			log.warn("传入的DistrOrderDTO为空");
			throw new RuntimeException("传入的DistrOrderDTO为空");
		}
		if (StringUtils.isBlank(distrOrderDTO.getReceiveAddr())) {
			log.warn("收件人地址不存在");
			throw new RuntimeException("收件人地址不存在");
		}
		if (StringUtils.isBlank(distrOrderDTO.getReceiveCode())) {
			log.warn("收件人地址邮编不存在");
			throw new RuntimeException("收件人地址邮编不存在");
		}
		if (StringUtils.isBlank(distrOrderDTO.getReceiveName())) {
			log.warn("收件人姓名不存在");
			throw new RuntimeException("收件人姓名不存在");
		}
		if (StringUtils.isBlank(distrOrderDTO.getReceivePhoneNo())) {
			log.warn("收件人手机电话不存在");
			throw new RuntimeException("收件人手机电话不存在");
		}
		for (DistrOrderDetailDTO distrOrderDetailDTO : distrOrderDTO.getList()) {
			if (StringUtils.isBlank(distrOrderDetailDTO.getSkuDesc())) {
				log.warn("最小库存描述不存在");
				throw new RuntimeException("最小库存描述不存在");
			}
			if (StringUtils.isBlank(distrOrderDetailDTO.getSubjectName())) {
				log.warn("商品名称不存在");
				throw new RuntimeException("商品名称不存在");
			}
			if (distrOrderDetailDTO.getSkuCount() == null) {
				log.warn("商品数量不存在");
				throw new RuntimeException("商品数量不存在");
			}
			if (distrOrderDetailDTO.getSkuId() == null) {
				log.warn("最小库存id不存在");
				throw new RuntimeException("最小库存id不存在");
			}
			if (distrOrderDetailDTO.getSubjectId() == null) {
				log.warn("商品id不存在");
				throw new RuntimeException("商品id不存在");
			}
		}
		if (distrOrderDTO.getReceiverId() == null) {
			log.warn("买家id不存在");
			throw new RuntimeException("买家id不存在");
		}

		if (distrOrderDTO.getTradeOrderId() == null) {
			log.warn("交易系统订单id不存在");
			throw new RuntimeException("交易系统订单id不存在");
		}
	}

	private Integer saveToDistrOrderDetailAndRelation(
			DistrOrderDTO distrOrderDTO) {
		checkDistrOrderDTO(distrOrderDTO);
		DistrOrder dorder = saveToDistrOrder(distrOrderDTO);
		saveToDistrOrderDetail(dorder.getDistrOrderId(), distrOrderDTO);
		saveToDistrOrderRelation(dorder.getDistrOrderId(), distrOrderDTO);
		return dorder.getDistrOrderId();
	}
	
	//检查传入的DistrBatchOutOfStockResponseDTO
	private void checkDistrBatchOutOfStockResponseDTO(DistrBatchOutOfStockResponseDTO dbrDTO){
		if (dbrDTO == null){
			log.warn("传入的DistrBatchOutOfStockResponseDTO为空");
			throw new RuntimeException("传入的DistrBatchOutOfStockResponseDTO为空");
		}
		if (StringUtils.isBlank(dbrDTO.getStorageRef())){
			log.warn("流水号为空");
			throw new RuntimeException("流水号为空");
		}
		List<DistrBatchOutOfStockResponseDetail> list = dbrDTO.getList();
		if (list == null||list.isEmpty()){
			log.warn("缺货补配明细列表为空");
			throw new RuntimeException("缺货补配明细列表为空");
		}
		for (DistrBatchOutOfStockResponseDetail detail : list) {
			if (StringUtils.isBlank(detail.getPosLabel())){
				log.warn("库位号为空");
				throw new RuntimeException("库位号为空");
			}
			if (detail.getSkuCount() == null){
				log.warn("数量为空");
				throw new RuntimeException("数量为空");
			}
			if (detail.getSkuId() == null){
				log.warn("最小库存单元id为空");
				throw new RuntimeException("最小库存单元id为空");
			}
		}
	}
	
	// 添加配货批次表
	private Integer saveToDistrBatch(DistrBatchOutOfStockResponseDTO dbrDTO,Integer operator) {
		checkDistrBatchOutOfStockResponseDTO(dbrDTO);
		checkInteger(operator);
		DistrBatch dbatch = new DistrBatch();
		dbatch.setOperator(operator);
		dbatch.setStorageRef(dbrDTO.getStorageRef());
		dbatch.setGmtCreate(DateFormat.getCurrentTime());
		dbatch.setGmtModify(DateFormat.getCurrentTime());
		dbatch.setVersion(1);
		distrBatchDao.save(dbatch);
		log.info("成功添加配货批次表，批次id为{}", dbatch.getDistrBatchId());
		return dbatch.getDistrBatchId();
	}

	// 将批次修改到配货系统订单里
	private Integer updateDistrOrderBatch(DistrBatchOutOfStockResponseDTO dbrDTO, Integer operator) {
		checkDistrBatchOutOfStockResponseDTO(dbrDTO);
		checkInteger(operator);
		// 添加配货批次表，得到批次id
		Integer distrBatchId = saveToDistrBatch(dbrDTO, operator);
		if (distrBatchId == null) {
			log.warn("未成功添加进配货批次表");
			throw new RuntimeException("未成功添加进配货批次表");
		}
		DistrOrder order = new DistrOrder();
		order.setDistrStatus(DistrOrderStatus.WAIT_DISTRIBUTION);
		List<DistrOrder> list = distrOrderDao.findByExample(order);
		if (list == null || list.isEmpty()) {
			log.warn("没有找到等待配货的订单");
			throw new RuntimeException("没有找到等待配货的订单");
		}
		for (DistrOrder distrOrder : list) {
			distrOrder.setDistrBatchId(distrBatchId);
			// 修改配货状态
			distrOrder.setDistrStatus(DistrOrderStatus.PICKING_DISTRIBUTION);
			updateDistrOrder(distrOrder);
			log.info("成功修改配发系统id={}的状态为正在配货", distrOrder.getDistrOrderId());
		}
		return distrBatchId;
	}

	/**
	 * 调用仓储接口，查询商品的库位信息
	 * @param isAgain 是否二次查询（二次查询只用返回之前的查询结果，不用重新配货）
	 * @return
	 */
	private DistrBatchOutOfStockResponseDTO queryStorageInfo(List<DistrBatchRequestDTO> requestList, boolean isAgain) {
		// TODO 调用库存接口,对返回结果进行参数判断
		DistrBatchOutOfStockResponseDTO dbrDTO = new DistrBatchOutOfStockResponseDTO();
		dbrDTO.setStorageRef("QWER00324");
		DistrBatchOutOfStockResponseDetail dbrd = new DistrBatchOutOfStockResponseDetail();
		dbrd.setSkuCount(100);
		dbrd.setPosLabel("A001");
		dbrd.setSkuId(3);
		List<DistrBatchOutOfStockResponseDetail> list = new ArrayList<DistrBatchOutOfStockResponseDetail>();
		list.add(dbrd);
		dbrDTO.setList(list);
		dbrDTO.setSuccess(true);
		dbrDTO.setFailReason("");
		return dbrDTO;
	}

	// 调用缺货补配接口
	private DistrBatchOutOfStockResponseDTO getOutOfStockResponse(DistrBatchOutOfStockRequestDTO requestDTO) {
		// TODO 调用缺货补配接口，检查参数
		DistrBatchOutOfStockResponseDTO osrDTO = new DistrBatchOutOfStockResponseDTO();
		osrDTO.setStorageRef("QWER1002");
		osrDTO.setSuccess(false);
		osrDTO.setFailReason("库存不够");

		List<DistrBatchOutOfStockResponseDetail> list = new ArrayList<DistrBatchOutOfStockResponseDetail>();
		DistrBatchOutOfStockResponseDetail osrDetail = new DistrBatchOutOfStockResponseDetail();
		osrDetail.setPosLabel("A001");
		osrDetail.setSkuCount(20);
		osrDetail.setSkuId(3);
		list.add(osrDetail);
		osrDTO.setList(list);
		return osrDTO;
	}
	
	//检查distrOrderIdList
	private void checkIdList(List<Integer> distrOrderIdList){
		if (distrOrderIdList == null||distrOrderIdList.isEmpty()){
			log.warn("传入的idList为空");
			throw new RuntimeException("传入的idList为空");
		}
	}
	
	// 仓储信息转换为用于展示的信息(添加商品信息)
	private List<DistrBatchPrintDTO> transformStorageInfoToDisplayList(DistrBatchOutOfStockResponseDTO dbrDTO) {
		List<DistrBatchPrintDTO> printList = new ArrayList<DistrBatchPrintDTO>();
		for (DistrBatchOutOfStockResponseDetail responseDetail : dbrDTO.getList()) {
			DistrBatchPrintDTO printDTO = new DistrBatchPrintDTO();
			BeanUtils.copyProperties(responseDetail, printDTO);
			DistrOrderDetail detail = new DistrOrderDetail();
			Integer skuId = responseDetail.getSkuId();
			detail.setSkuId(skuId);
			// XXX 以后改用查询商品模块
			List<DistrOrderDetail> detailList = distrOrderDetailDao.findByExample(detail);
			if (detailList == null || detailList.isEmpty()){
				log.warn("订单明细表中skuId为{}的记录不存在",skuId);
				throw new RuntimeException("订单明细表中skuId为"+skuId+"的记录不存在");
			}
			printDTO.setSkuDesc(detailList.get(0).getSkuDesc());
			printDTO.setSubjectName(detailList.get(0).getSubjectName());
			printList.add(printDTO);
		}
		return printList;
	}
	
	/**
	 * 查询配货单-初次配货
	 * @param operator
	 * @param distrOrderIdList
	 * @return List<DistrBatchPrintDTO>
	 */
	public List<DistrBatchPrintDTO> findDistrOrderList(Integer operator,
			List<Integer> distrOrderIdList) {
		return findDistrOrderList(operator, distrOrderIdList, false);
	}

	private List<DistrBatchRequestDTO> getRequestList(List<Integer> distrOrderIdList) {
		// 请求参数
		List<DistrBatchRequestDTO> requestList = new ArrayList<DistrBatchRequestDTO>();
		Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
		for (Integer distrOrderId : distrOrderIdList) {
			List<DistrOrderDetail> detailList = findDistrOrderDetailByDistrOrderId(distrOrderId);
			for (DistrOrderDetail orderDetail : detailList) {
				mergeCountMap(countMap, orderDetail.getSkuId(),
						orderDetail.getSkuCount());
			}
		}
		for (Entry<Integer, Integer> entry : countMap.entrySet()) {
			requestList.add(new DistrBatchRequestDTO(entry.getKey(), entry.getValue()));
		}
		return requestList;
	}

	/**
	 * 查询配货单
	 * @param operator
	 * @param distrOrderIdList
	 * @return List<DistrBatchPrintDTO>
	 */
	public List<DistrBatchPrintDTO> findDistrOrderList(Integer operator,
			List<Integer> distrOrderIdList, boolean isAgain) {
		checkInteger(operator);
		checkIdList(distrOrderIdList);
		// XXX 查询所有订单，对相同sku进行数量合并，以便调用仓储模块接口进行查询
		List<DistrBatchRequestDTO> requestList = getRequestList(distrOrderIdList);
		// 调用仓储系统接口，查询商品所在库位信息
		DistrBatchOutOfStockResponseDTO dbrDTO = queryStorageInfo(requestList,isAgain);
		// 将批次修改到配货系统订单里
		if (!isAgain){
			Integer distrBatchId = updateDistrOrderBatch(dbrDTO, operator);
			log.info("成功添加配货批次为{}到配发系统订单里", distrBatchId);
		}
		return transformStorageInfoToDisplayList(dbrDTO);
	}

	private void mergeCountMap(Map<Integer, Integer> countMap, Integer skuId,
			Integer skuCount) {
		Integer currentCount = countMap.get(skuId);
		if(currentCount == null) {
			currentCount = 0;
		}
		currentCount += skuCount;
		countMap.put(skuId, currentCount);
	}

	private List<DistrOrderDetail> findDistrOrderDetailByDistrOrderId(
			Integer distrOrderId) {
		DistrOrderDetail detail = new DistrOrderDetail();
		detail.setDistrOrderId(distrOrderId);
		return distrOrderDetailDao.findByExample(detail);
	}
	
	private DistrBatchOutOfStockRequestDetail transformListToRequestDTO(DistrBatchPrintDTO distrBatchPrintDTO){
		DistrBatchOutOfStockRequestDetail detail = new DistrBatchOutOfStockRequestDetail();
		detail.setPosLabel(distrBatchPrintDTO.getPosLabel());
		detail.setSkuCount(distrBatchPrintDTO.getSkuCount());
		return detail;
	}
	
	private void checkDistrBatchPrintDTO(List<DistrBatchPrintDTO> printRequestList){
		if (printRequestList == null || printRequestList.isEmpty()){
			log.warn("传入的list为空");
			throw new RuntimeException("传入的list为空");
		}
	}
	
	//缺货补配请求参数
	private DistrBatchOutOfStockRequestDTO getOutOfStockRequestDTO(List<DistrBatchPrintDTO> printRequestList){
		DistrBatchOutOfStockRequestDTO requestDTO = new DistrBatchOutOfStockRequestDTO();
		List<DistrBatchOutOfStockRequestDetail> detailList = new ArrayList<DistrBatchOutOfStockRequestDetail>();
		for (DistrBatchPrintDTO distrBatchPrintDTO : printRequestList) {
			if (requestDTO.getSkuId() != null){
				detailList.add(transformListToRequestDTO(distrBatchPrintDTO));
			}else {
				requestDTO.setSkuId(distrBatchPrintDTO.getSkuId());
				requestDTO.setStorageRef(distrBatchPrintDTO.getStorageRef());
				detailList.add(transformListToRequestDTO(distrBatchPrintDTO));
				requestDTO.setList(detailList);
			}
		}
		return requestDTO;
	}
	
	/**
	 * 缺货补配
	 * @param outOfStockRequest
	 * @return List<DistrBatchPrintDTO>
	 */
	public List<DistrBatchPrintDTO> outOfStockOrderList(List<DistrBatchPrintDTO> printRequestList) {
		checkDistrBatchPrintDTO(printRequestList);
		DistrBatchOutOfStockRequestDTO requestDTO = getOutOfStockRequestDTO(printRequestList);
		// 查询所有订单，对相同sku进行数量合并，以便调用仓储模块接口进行查询
		// 调用storage缺货补配，多种商品缺货，循环调用接口，整合完结果后返回web层
		DistrBatchOutOfStockResponseDTO osrDTO = getOutOfStockResponse(requestDTO);
		return transformStorageInfoToDisplayList(osrDTO);
	}
	
	//检查DistrExpressPreDTO
	private void checkDistrExpressPreDTO(DistrExpressPreDTO preDTO){
		if (preDTO == null){
			log.warn("传入的DistrExpressPreDTO为空");
			throw new RuntimeException("传入的DistrExpressPreDTO为空");
		}
		if (preDTO.getDistrOrderId() == null){
			log.warn("传入的配发系统订单号为空");
			throw new RuntimeException("传入的配发系统订单号为空");
		}
		if (StringUtils.isBlank(preDTO.getExpressNo())){
			log.warn("传入的快递单号为空");
			throw new RuntimeException("传入的快递单号为空");
		}
	}
	
	/**
	 * 保存快递单号
	 * @param preDTO
	 */
	public void saveExpressNo(DistrExpressPreDTO preDTO) {
		checkDistrExpressPreDTO(preDTO);
		DetachedCriteria criteria = DetachedCriteria.forClass(DistrOrder.class);
		criteria.add(Restrictions.eq("distrOrderId", preDTO.getDistrOrderId()));
		criteria.add(Restrictions.ne("distrStatus", DistrOrderStatus.EXPORTING_DISTRIBUTION));
		List<DistrOrder> orderList = distrOrderDao
				.findByCriteria(criteria);
		if (orderList == null || orderList.isEmpty()) {
			log.warn("系统订单配发表中不存在该主键id={}", preDTO.getDistrOrderId());
			throw new RuntimeException("系统订单配发表中不存在id为"+preDTO.getDistrOrderId()+",订单状态不为已出库的订单");
		}
		// 设置快递单号和修改快递单状态
		DistrOrder order = orderList.get(0);
		order.setExpressNo(preDTO.getExpressNo());
		order.setExpressStatus(ExpressStatus.WAITPRINT_EXPORT);
		updateDistrOrder(order);
		log.info("保存快递单号成功，订单号{}，快递单号{}", order.getDistrOrderId(),
				order.getExpressNo());
	}
	
	/**
	 * 查询快递面单信息(打印/重新打印快递单时候使用)
	 * 
	 * @param preDTO
	 * @return DistrExpressDTO
	 */
	public DistrExpressDTO queryExpressInfo(DistrExpressPreDTO preDTO) {
		checkDistrExpressPreDTO(preDTO);
		DistrOrder distrOrder = distrOrderDao
				.findById(preDTO.getDistrOrderId());
		if (distrOrder == null) {
			log.warn("配发系统订单id={}不存在", preDTO.getDistrOrderId());
			throw new RuntimeException("系统订单配发表中不存在该id"
					+ preDTO.getDistrOrderId());
		}
		// TODO 商家固定的联系方式
		DistrExpressDTO deDTO = new DistrExpressDTO();
		BeanUtils.copyProperties(distrOrder, deDTO);
		deDTO.setExpressNo(preDTO.getExpressNo());
		deDTO.setSenderAddr("湖北武汉");
		deDTO.setSenderCode("13243");
		deDTO.setSenderName("张三");
		deDTO.setSenderPhoneNo("102837748585");
		List<DistrOrderDetail> detailList = findDistrOrderDetailByDistrOrderId(distrOrder.getDistrOrderId());
		List<DistrExpressDetailDTO> edList = new ArrayList<DistrExpressDetailDTO>();
		for (DistrOrderDetail distrOrderDetail : detailList) {
			DistrExpressDetailDTO detailDTO = new DistrExpressDetailDTO();
			BeanUtils.copyProperties(distrOrderDetail, detailDTO);
			edList.add(detailDTO);
		}
		deDTO.setList(edList);
		DistrOrder order = distrOrderDao.findById(preDTO.getDistrOrderId());
		order.setExpressStatus(ExpressStatus.WRITTEN_EXPORT);
		updateDistrOrder(order);
		log.info("查询完快递单面单信息后成功更新快递单状态为已打印，订单id为{}",preDTO.getDistrOrderId());
		return deDTO;
	}

	private void updateDistrOrder(DistrOrder order) {
		order.setGmtModify(DateFormat.getCurrentTime());
		order.setVersion(order.getVersion()+1);
		distrOrderDao.update(order);
	}

	//检查String类型
	private void checkString(String expressCompany){
		if (StringUtils.isBlank(expressCompany)){
			log.warn("传入的String类型为空");
			throw new RuntimeException("传入的String类型为空");
		}
	}
	
	/**
	 * 切换快递公司
	 * 
	 * @param distrOrderId
	 * @param expressCompany
	 */
	public Integer changeExpressCompany(Integer distrOrderId, String expressCompany) {
		checkInteger(distrOrderId);
		checkString(expressCompany);
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		if (DistrOrderStatus.EXPORTING_DISTRIBUTION.equals(distrOrder.getDistrStatus())){
			log.warn("订单id为{}的订单已出库，不能切换快递公司",distrOrderId);
			throw new RuntimeException("订单id为"+distrOrderId+"的订单已出库，不能切换快递公司");
		}
		if (ExpressStatus.WRITTEN_EXPORT.equals(distrOrder.getExpressStatus())){
			log.warn("订单id为{}的订单已打印，不能切换快递公司",distrOrderId);
			throw new RuntimeException("订单id为"+distrOrderId+"的订单已打印，不能切换快递公司");
		}
		distrOrder.setExpressCompany(expressCompany);
		updateDistrOrder(distrOrder);
		log.info("成功修改配发系统订单id={}的记录的快递公司为{}", distrOrderId, expressCompany);
		String detail = "快递公司切换为"+expressCompany;
		Integer changeId = saveToDistrChange(distrOrderId, detail,DistrChangeType.CHANGE_COMPANY);
		log.info("成功将id为"+distrOrderId+"的订单添加到变更记录表中");
		return changeId;
	}

	// 检查订单是否通过
	private CheckResultDTO checkIfOrderChanged(int distrOrderId) {
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		if(distrOrder == null) {
			return buildCheckResultDTO(false, "订单不存在，distrOrderId=" + distrOrderId);
		}
		// 取消的订单不通过
		if (DistrOrderStatus.ORDER_STATUS_CANCEL.equals(distrOrder.getDistrStatus())) {
			return buildCheckResultDTO(false, "订单已被取消");
		}
		// 拦截的订单不通过
		if (DistrOrderStatus.BLOCK_DISTRIBUTION.equals(distrOrder.getDistrStatus())){
			return buildCheckResultDTO(false, "订单已被拦截");
		}
		// 发生变更的订单(改地址/改快递公司等)不通过
		if (distrOrder.getChangeOrderFlag()) {
			return buildCheckResultDTO(false, "订单发生了变更");
		}
		return buildCheckResultDTO(true, "");
	}
	
	private CheckResultDTO buildCheckResultDTO(boolean isSuccess, String reason) {
		CheckResultDTO result = new CheckResultDTO();
		result.setSuccess(isSuccess);
		result.setResult(reason);
		return result;
	}

	// 通过快递单号检查
	private CheckResultDTO checkOrderByExpressNo(String expressNo) {
		checkString(expressNo);
		Integer distrOrderId = findDistrOrderIdByExpressNo(expressNo);
		CheckResultDTO resultDTO = new CheckResultDTO();
		resultDTO =  checkIfOrderChanged(distrOrderId);
		if (!resultDTO.isSuccess()){
			return resultDTO;
		}
		return resultDTO;
	}

	// 通过快递单号得到distrOrderId
	private Integer findDistrOrderIdByExpressNo(String expressNo) {
		DistrOrder order = this.findDistrOrderByExpressNo(expressNo);
		return order == null ? null : order.getDistrOrderId();
	}
	
	private DistrOrder findDistrOrderByExpressNo(String expressNo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(DistrOrder.class);
		criteria.add(Restrictions.eq("expressNo", expressNo));
		criteria.add(Restrictions.ne("distrStatus", DistrOrderStatus.ORDER_STATUS_CANCEL));
		List<DistrOrder> list = distrOrderDao.findByCriteria(criteria);
		if (list == null || list.isEmpty()) {
			return null;
		}
		if (list.size()>1){
			log.warn("快递单为{}的快递单号对应多条订单",expressNo);
			throw new RuntimeException("快递单为"+expressNo+"的快递单号对应多条订单");
		}
		return list.get(0);
	}

	//检查DistrMapDTO
	private void checkDistrMapDTO(DistrMapDTO mapDTO){
		if (mapDTO == null){
			log.warn("传入的DistrMapDTO为空");
			throw new RuntimeException("传入的DistrMapDTO为空");
		}
	}
	/**
	 * 分拣先扫描快递单号将结果存入session中
	 * 
	 * 检查订单状态是否变化
	 * 检查地址是否变化
	 * 输入expressNo和skuId，检查分拣结果和系统记录是否一致
	 * 通过后修改订单状态
	 * 返回ResultDTO，包含不通过原因
	 * @param expressNo
	 * @param mapDTO
	 * @return CheckResultDTO
	 */
	public CheckResultDTO checkExpressNo(String expressNo,
			DistrMapDTO mapDTO) {
		checkString(expressNo);
		checkDistrMapDTO(mapDTO);
		Integer distrOrderId = findDistrOrderIdByExpressNo(expressNo);
		DistrOrder dorder = distrOrderDao.findById(distrOrderId);
		//若订单状态为已出库，则抛异常
		if (DistrOrderStatus.EXPORTING_DISTRIBUTION.equals(dorder.getDistrStatus())){
			log.warn("快递单号为{}的订单已出库",expressNo);
			throw new RuntimeException("快递单号为"+expressNo+"的订单已出库");
		}
		// 检查是否重复配货，状态带扫描
		if (DistrOrderStatus.CHECKOK_DISTRIBUTION.equals(dorder.getDistrStatus())) {
			log.warn("快递单号为{}的记录已被成功扫描过", expressNo);
			throw new RuntimeException("已扫描成功过该订单，快递单号为" + expressNo);
		}
		List<DistrOrderDetail> detailList = findDistrOrderDetailByDistrOrderId(distrOrderId);
		if (detailList == null||detailList.isEmpty()){
			log.warn("配发系统明细表中不存在配发系统id为{}的明细",distrOrderId);
			throw new RuntimeException("配发系统明细表中不存在配发系统id为"+distrOrderId+"的明细");
		}
		
		Map<Integer,DistrGoodsDTO> goodsMap = new HashMap<Integer, DistrGoodsDTO>();
		for (DistrOrderDetail distrOrderDetail : detailList) {
			DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
			BeanUtils.copyProperties(distrOrderDetail, goodsDTO);
			goodsMap.put(goodsDTO.getSkuId(), goodsDTO);
		}
		mapDTO.setComparedMap(goodsMap);
		mapDTO.setScannedMap(new HashMap<Integer,DistrGoodsDTO>());
		return checkOrderByExpressNo(expressNo);
	}

	
	/**
	 * 检查分拣结果和系统记录是否一致
	 * @param goodDTO
	 * @param mapDTO
	 * @return CheckResultDTO
	 */
	public CheckResultDTO checkGoodsMatch(DistrGoodsDTO goodDTO,
			DistrMapDTO mapDTO) {
		checkDistrMapDTO(mapDTO);
		if (goodDTO.getDistrOrderId() == null){
			throw new RuntimeException("传入的配发系统订单id为空");
		}
		if (goodDTO.getSkuCount() == null){
			throw new RuntimeException("传入的商品数量为空");
		}
		if (StringUtils.isBlank(goodDTO.getSkuDesc())){
			throw new RuntimeException("传入的最小单元描述为空");
		}
		CheckResultDTO result = new CheckResultDTO();
		Map<Integer,DistrGoodsDTO> comparedMap = mapDTO.getComparedMap();
		Map<Integer, DistrGoodsDTO> scannedMap = mapDTO.getScannedMap();

		// 从mapDTO中取得scannedEntry，然后数量+1
		DistrGoodsDTO scannedEntry = getScannedEntryFromMap(scannedMap,
				goodDTO.getSkuId());
		scannedEntry.setSkuCount(scannedEntry.getSkuCount() + 1);

		int key = goodDTO.getSkuId();
		DistrGoodsDTO dbEntry = comparedMap.get(key);
		if (dbEntry == null) {
			result.setSuccess(false);
			result.setResult("扫描货物不在系统订单中");
			return result;
		}

		if (scannedEntry.getSkuCount() < dbEntry.getSkuCount()) {
			result.setSuccess(true);
			result.setResult("扫描数量小于系统数量");
			return result;
		}
		if (scannedEntry.getSkuCount() > dbEntry.getSkuCount()) {
			result.setSuccess(false);
			result.setResult("扫描数量超过系统数量");
			return result;
		}
		// 扫描数量 == 系统数量
		for (Integer comparedKey : comparedMap.keySet()) {
			DistrGoodsDTO goodsDTO = scannedMap.get(comparedKey);
			if (goodsDTO == null){
				result.setSuccess(true);
				result.setResult("系统记录中的其他货物还未被扫描");
				return result;
			}
			if (goodsDTO.getSkuCount() != comparedMap.get(comparedKey).getSkuCount()) {
				result.setSuccess(true);
				result.setResult("扫描数量等于系统数量,其他未扫描的数量不匹配");
				return result;
			}
		}
		int distrOrderId = dbEntry.getDistrOrderId();
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		distrOrder.setDistrStatus(DistrOrderStatus.CHECKOK_DISTRIBUTION);
		updateDistrOrder(distrOrder);
		log.info("成功扫描通过该商品，最小库存单元id为{}", goodDTO.getSkuId());
		result.setSuccess(true);
		result.setResult("该快递单与系统匹配一致");
		return result;
	}

	private DistrGoodsDTO getScannedEntryFromMap(
			Map<Integer, DistrGoodsDTO> scannedMap, Integer skuId) {
		DistrGoodsDTO scannedEntry = scannedMap.get(skuId);
		if (scannedEntry == null) {
			scannedEntry = new DistrGoodsDTO();
			scannedEntry.setSkuId(skuId);
			scannedEntry.setSkuCount(0);
			scannedMap.put(skuId, scannedEntry);
		}
		return scannedEntry;
	}

	// 出库处理
	private void sendOutByDistrOrderId(int distrOrderId) {
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		if (!DistrOrderStatus.CHECKOK_DISTRIBUTION.equals(distrOrder.getDistrStatus())
				&& !DistrOrderStatus.PICKING_DISTRIBUTION.equals(distrOrder.getDistrStatus())) {
			log.warn("订单id为{}的订单未检验成功，不能出库", distrOrderId);
			throw new RuntimeException("订单id为" + distrOrderId + "的订单未检验成功，不能出库");
		}
		distrOrder.setDistrStatus(DistrOrderStatus.EXPORTING_DISTRIBUTION);
		updateDistrOrder(distrOrder);
		log.info("成功修改配发系统订单id为{}的记录的配发状态为已出库", distrOrderId);
		// TODO 调用tradebase通知交易发货
	}

	/**
	 * 出库，通过快递单号
	 * 
	 * 检查订单状态是否变化
	 * 检查地址是否变化
	 * 通过后修改订单状态
	 * 调用tradebase通知交易发货
	 * @param expressNo
	 * @return CheckResultDTO
	 */
	public CheckResultDTO sendOutByExpressNo(String expressNo) {
		checkString(expressNo);
		Integer distrOrderId = findDistrOrderIdByExpressNo(expressNo);
		CheckResultDTO result = checkOrderByExpressNo(expressNo);
		if (result.isSuccess()) {
			sendOutByDistrOrderId(distrOrderId);
		}
		return result;
	}

	/**
	 * 出库，通过订单id
	 * 
	 * @param distrOrderId
	 * @return CheckResultDTO
	 */
	public void sendOutByDistrOrderId(List<Integer> distrOrderIds) {
		checkIdList(distrOrderIds);
		if (distrOrderIds == null || distrOrderIds.isEmpty()) {
			log.warn("配发系统订单id为空");
			throw new RuntimeException("配发系统订单id为空");
		}
		for (int i = 0; i < distrOrderIds.size(); i++) {
			CheckResultDTO resultDTO = checkIfOrderChanged(distrOrderIds.get(i));
			if(!resultDTO.isSuccess()) {
				throw new RuntimeException(resultDTO.getResult());
			}
			sendOutByDistrOrderId(distrOrderIds.get(i));
		}
	}

	/**
	 * 取消发货单
	 * 
	 * @param distrOrderId
	 */
	public void cancelDistr(int distrOrderId) {
		checkInteger(distrOrderId);
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		if (DistrOrderStatus.EXPORTING_DISTRIBUTION.equals(distrOrder
				.getDistrStatus())) {
			log.warn("配发系统订单中id为{}的订单已出库", distrOrderId);
			throw new RuntimeException("该订单已出库, distrOrderId=" + distrOrderId);
		}
		if (DistrOrderStatus.ORDER_STATUS_CANCEL.equals(distrOrder
				.getDistrStatus())) {
			return;
		}
		distrOrder.setDistrStatus(DistrOrderStatus.ORDER_STATUS_CANCEL);
		updateDistrOrder(distrOrder);
		log.info("成功修改配发系统订单id为{}的订单状态为已取消", distrOrderId);
		String detail = "订单为"+distrOrderId+"的订单已被取消";
		saveToDistrChange(distrOrderId, detail,DistrChangeType.ORDER_CANCELED);
		log.info("订单为{}的订单已被取消",distrOrderId);
	}

	public DistrBatchDao getDistrBatchDao() {
		return distrBatchDao;
	}

	public void setDistrBatchDao(DistrBatchDao distrBatchDao) {
		this.distrBatchDao = distrBatchDao;
	}

	public DistrOrderDao getDistrOrderDao() {
		return distrOrderDao;
	}

	public void setDistrOrderDao(DistrOrderDao distrOrderDao) {
		this.distrOrderDao = distrOrderDao;
	}

	public DistrOrderDetailDao getDistrOrderDetailDao() {
		return distrOrderDetailDao;
	}

	public void setDistrOrderDetailDao(DistrOrderDetailDao distrOrderDetailDao) {
		this.distrOrderDetailDao = distrOrderDetailDao;
	}

	public DistrChangeDao getDistrChangeDao() {
		return distrChangeDao;
	}

	public void setDistrChangeDao(DistrChangeDao distrChangeDao) {
		this.distrChangeDao = distrChangeDao;
	}

	public DistrOrderRelationDao getDistrOrderRelationDao() {
		return distrOrderRelationDao;
	}

	public void setDistrOrderRelationDao(
			DistrOrderRelationDao distrOrderRelationDao) {
		this.distrOrderRelationDao = distrOrderRelationDao;
	}
}
