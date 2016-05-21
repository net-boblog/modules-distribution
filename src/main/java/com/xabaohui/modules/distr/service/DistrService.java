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
	 * �������֪ͨ
	 * 
	 * @param distrChangeDTO
	 * @return distrOrderId
	 */
	//��鴫��Ľ��׶���id
	private void checkTradeOrderId(Integer tradeOrderId){
		if (tradeOrderId == null){
			throw new RuntimeException("����Ľ��׶���idΪ��");
		}
	}
	
	//ͨ�����׶���id�õ��䷢ϵͳ��������
	private DistrOrder findDistrOrderByTradeOrderId(Integer tradeOrderId){
		DistrOrderRelation relation = new DistrOrderRelation();
		relation.setTradeOrderId(tradeOrderId);
		List<DistrOrderRelation> relationList = distrOrderRelationDao.findByExample(relation);
		if (relationList == null || relationList.isEmpty()){
			log.warn("����idΪ{}�����Ķ���id������",tradeOrderId);
			throw new RuntimeException("����idΪ"+tradeOrderId+"�����Ķ���id������");
		}
		if (relationList.size()>1){
			log.warn("����idΪ{}�����Ķ���id�ж�����¼",tradeOrderId);
			throw new RuntimeException("����idΪ"+tradeOrderId+"�����Ķ���id�ж�����¼");
		}
		Integer distrOrderId =  relationList.get(0).getDistrOrderId();
		return distrOrderDao.findById(distrOrderId);
	}
	
	private void assertDistrOrderNotSend(DistrOrder distrOrder) {
		if (distrOrder == null) {
			log.warn("ϵͳ�����䷢����������");
			throw new RuntimeException("ϵͳ�����䷢����������");
		}
		if (DistrOrderStatus.EXPORTING_DISTRIBUTION.equals(distrOrder
				.getDistrStatus())) {
			log.warn("����idΪ{}���䷢ϵͳ�����ѷ������޷����ظö���", distrOrder.getDistrOrderId());
			throw new RuntimeException("�ö����ѷ������޷����ظö���");
		}
	}
	
	private void checkAddrDTO(DistrOrderAddrDTO addrDTO){
		if (addrDTO == null){
			log.warn("����ĵ�ַDTOΪ��");
			throw new RuntimeException("����ĵ�ַDTOΪ��");
		}
		if (StringUtils.isBlank(addrDTO.getReceiveAddr())){
			log.warn("������ռ��˵�ַΪ��");
			throw new RuntimeException("������ռ��˵�ַΪ��");
		}
		if (StringUtils.isBlank(addrDTO.getReceiveCode())){
			log.warn("������ռ����ʱ�Ϊ��");
			throw new RuntimeException("������ռ����ʱ�Ϊ��");
		}
		if (StringUtils.isBlank(addrDTO.getReceiveName())){
			log.warn("������ռ�������Ϊ��");
			throw new RuntimeException("������ռ�������Ϊ��");
		}
		if (StringUtils.isBlank(addrDTO.getReceivePhoneNo())){
			log.warn("������ռ����ֻ���Ϊ��");
			throw new RuntimeException("������ռ����ֻ���Ϊ��");
		}
		if (addrDTO.getReceiveCityId() == null){
			log.warn("����ĳ���idΪ��");
			throw new RuntimeException("����ĳ���idΪ��");
		}
	}
	
	/**
	 * ��ַ����
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
		// TODO ����IDӦ�û��ɳ�������
		String fmt = "����idΪ{}�Ķ�����ַ�޸�Ϊ�ռ�������Ϊ{}���ռ���ַ�ʱ�Ϊ{}���ռ����ֻ���Ϊ{}���ռ�����idΪ{}���ռ��˵�ַΪ{}";
		String infos = String.format(fmt, addrDTO.getReceiveName(), addrDTO.getReceiveCode(),
				addrDTO.getReceivePhoneNo(), addrDTO.getReceiveCityId(),
				addrDTO.getReceiveAddr());
		log.info(infos);
		Integer changeId = saveToDistrChange(tradeOrderId, infos, DistrChangeType.CHANGE_ADDR);
		log.info("����idΪ{}�Ķ�����ӵ������¼����", distrOrder.getDistrOrderId());
		return changeId;
	}
	
	/**
	 * �л���ݹ�˾
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
		log.info("����idΪ{}�Ķ���״̬Ϊ���л���ݹ�˾Ϊ{}",distrOrder.getDistrOrderId(),expressCompany);
		String detail = "��ݹ�˾�޸�Ϊ��" + expressCompany;
		Integer changeId = saveToDistrChange(tradeOrderId, detail, DistrChangeType.CHANGE_COMPANY);
		log.info("����idΪ{}�Ķ�����ӵ������¼����",distrOrder.getDistrOrderId());
		return changeId;
	}
	
	/**
	 * ���ض���
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
		log.info("����idΪ{}�Ķ���״̬Ϊ�ѱ�����",distrOrder.getDistrOrderId());
		String detail = "���ض�����" + memo;
		Integer changeId = saveToDistrChange(tradeOrderId, detail, DistrChangeType.ORDER_BLOCKED);
		log.info("����idΪ{}�Ķ�����ӵ������¼����",distrOrder.getDistrOrderId());
		return changeId;
	}
	
	/**
	 * ȡ�����ض���
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
			log.warn("idΪ{}���䷢ϵͳ����δ������,����״̬��������״̬", distrOrder.getDistrOrderId());
			throw new RuntimeException("�ö���δ������");
		}
		distrOrder.setDistrStatus(distrOrder.getPeblockStatus());
		updateDistrOrder(distrOrder);
		log.info("����idΪ{}�Ķ���״̬Ϊ��ȡ������",distrOrder.getDistrOrderId());
		String detail = "�ָ������صĶ�����" + memo;
		Integer distrChangeId = saveToDistrChange(tradeOrderId, detail, DistrChangeType.ORDER_UNBLOCKED);
		log.info("����idΪ{}�Ķ�����ӵ������¼����",distrOrder.getDistrOrderId());
		return distrChangeId;
	}
	
	/**
	 * ȡ������
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
		log.info("����idΪ{}�Ķ���״̬Ϊ��ȡ��",distrOrder.getDistrOrderId());
		String detail = "ȡ��������ԭ��Ϊ��" + memo;
		saveToDistrChange(distrOrder.getDistrOrderId(), detail, DistrChangeType.ORDER_CANCELED);
		log.info("����idΪ{}�Ķ�����ӵ������¼����",distrOrder.getDistrOrderId());
	}

	/**
	 * ��������
	 * 
	 * @param distrOrderDTO
	 * @return distrOrderId
	 */
	public Integer saveDistrOrder(DistrOrderDTO distrOrderDTO) {
		// �ж�DTO�ֶ��Ƿ�Ϊ��
		checkDistrOrderDTO(distrOrderDTO);
		// �ж��Ƿ��ظ����Ͷ���
		DistrOrderRelation relation = findDistrOrderRelationByTradeOrderId(distrOrderDTO.getTradeOrderId());
		if(relation != null) {
			throw new RuntimeException("�ظ����Ͷ���������ID=" + distrOrderDTO.getTradeOrderId());
		}
		
		// ͨ�����id,�ռ��˵�ַ���ж�״̬
		DistrOrder distrOrder = buildDistrOrderQueryExample(distrOrderDTO);
		distrOrder.setDistrStatus(DistrOrderStatus.WAIT_DISTRIBUTION);
		List<DistrOrder> list = distrOrderDao.findByExample(distrOrder);
		// ��û��wait״̬�����ݣ���ֱ�����
		if (list == null || list.isEmpty()) {
			Integer distrOrderId = saveToDistrOrderDetailAndRelation(distrOrderDTO);
			log.info("���ϵͳ�����䷢���䷢ϵͳ���������ϸ��ɹ����䷢ϵͳidΪ{}", distrOrderId);
			
			// ������ݿ����Ƿ���ͬ��ַͬ��ҵĶ���������У��������Ϊ�ѱ���������ڷּ�У��ͳ���׶ν�������
			markSameOrderChanged(distrOrderDTO);
			return distrOrderId;
		}
		if (list.size() > 1) {
			log.warn("���ݿ������쳣���ж����ȴ��������");
			throw new RuntimeException("���ݿ������쳣���ж����ȴ��������");
		}
		
		// �����ڵȴ�����Ķ���������кϲ�
		Integer distrOrderId = list.get(0).getDistrOrderId();
		saveToDistrOrderDetail(distrOrderId, distrOrderDTO);
		log.info("�����ϸ��ɹ��������䷢ϵͳ����idΪ{}", distrOrderId);
		saveToDistrOrderRelation(distrOrderId, distrOrderDTO);
		log.info("��ӹ�����ɹ�,�����䷢ϵͳ����idΪ{}", distrOrderId);
		
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
			throw new RuntimeException("���ݴ���DistrOrderRelation�ж�����¼��TradeOrderId=" + tradeOrderId);
		}
		return relationlist.get(0);
	}
	
	// ������ݿ����Ƿ���ͬ��ַͬ��ҵĶ���������У��������Ϊ�ѱ���������ڷּ�У��ͳ���׶ν�������
	private void markSameOrderChanged(DistrOrderDTO distrOrderDTO) {
		DistrOrder example = buildDistrOrderQueryExample(distrOrderDTO);
		List<DistrOrder> list = distrOrderDao.findByExample(example);
		if (list.size()>1){
			for (DistrOrder distrOrder : list) {
				distrOrder.setChangeOrderFlag(true);
				updateDistrOrder(distrOrder);
				log.warn("����idΪ{}�Ķ������ڵ�ַ�ظ���Ǳ��", distrOrder.getDistrOrderId());
				String detail = "����idΪ" + distrOrder.getDistrOrderId() + "�Ķ�����ַ���ظ�";
				saveToDistrChange(distrOrder.getDistrOrderId(), detail,
						DistrChangeType.ORDER_DUPLICATE_ADDR);
				log.info("����idΪ{}�Ķ������ڵ�ַ�ظ���Ǳ����ӵ������¼����",
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

	// ��Ӷ��������¼��
	private Integer saveToDistrChange(Integer distrOrderId,String detail,String changeType) {
		DistrChange distrChange = new DistrChange();
		distrChange.setDetail(detail);
		distrChange.setType(changeType);
		distrChange.setDistrOrderId(distrOrderId);
		distrChange.setGmtCreate(DateFormat.getCurrentTime());
		distrChange.setGmtModify(DateFormat.getCurrentTime());
		distrChange.setVersion(1);
		distrChangeDao.save(distrChange);
		log.info("idΪ{}���䷢ϵͳ����id�ɹ���ӵ������¼����", distrOrderId);
		return distrChange.getDistrChangeId();
	}

	private void saveDistrOrder(DistrOrder distrOrder){
		distrOrder.setGmtModify(DateFormat.getCurrentTime());
		distrOrder.setVersion(1);
		distrOrderDao.save(distrOrder);
	}
	// ������ϵͳ������
	private DistrOrder saveToDistrOrder(DistrOrderDTO distrOrderDTO) {
		checkDistrOrderDTO(distrOrderDTO);
		DistrOrder distrOrder = new DistrOrder();
		BeanUtils.copyProperties(distrOrderDTO, distrOrder);
		distrOrder.setDistrStatus(DistrOrderStatus.WAIT_DISTRIBUTION);
		distrOrder.setExpressStatus(ExpressStatus.WAITPRINT_EXPORT);
		distrOrder.setChangeOrderFlag(false);
		distrOrder.setGmtCreate(DateFormat.getCurrentTime());
		saveDistrOrder(distrOrder);
		log.info("�ɹ�������ϵͳ�������䷢ϵͳ����idΪ{}", distrOrder.getDistrOrderId());
		return distrOrder;
	}

	// ������ϵͳ������ϸ��
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
			log.info("�ɹ�����䷢ϵͳ��ϸ���䷢ϵͳ����idΪ{}", distrOrderId);
		}
		return detailList;
	}

	// ��Ӷ���������
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
		log.info("�ɹ���ӵ������������䷢ϵͳ����idΪ{}������ϵͳ����idΪ{}", distrOrderId,
				distrOrderDTO.getTradeOrderId());
	}
	
	//�쳵�����int���͵�����
	private void checkInteger(Integer distrOrderId){
		if (distrOrderId == null){
			log.warn("�����idΪ��");
			throw new RuntimeException("�����idΪ��");
		}
	}
	
	// ��֤��������dto��ֵ�Ƿ�Ϊ��
	private void checkDistrOrderDTO(DistrOrderDTO distrOrderDTO) {
		if (distrOrderDTO == null){
			log.warn("�����DistrOrderDTOΪ��");
			throw new RuntimeException("�����DistrOrderDTOΪ��");
		}
		if (StringUtils.isBlank(distrOrderDTO.getReceiveAddr())) {
			log.warn("�ռ��˵�ַ������");
			throw new RuntimeException("�ռ��˵�ַ������");
		}
		if (StringUtils.isBlank(distrOrderDTO.getReceiveCode())) {
			log.warn("�ռ��˵�ַ�ʱ಻����");
			throw new RuntimeException("�ռ��˵�ַ�ʱ಻����");
		}
		if (StringUtils.isBlank(distrOrderDTO.getReceiveName())) {
			log.warn("�ռ�������������");
			throw new RuntimeException("�ռ�������������");
		}
		if (StringUtils.isBlank(distrOrderDTO.getReceivePhoneNo())) {
			log.warn("�ռ����ֻ��绰������");
			throw new RuntimeException("�ռ����ֻ��绰������");
		}
		for (DistrOrderDetailDTO distrOrderDetailDTO : distrOrderDTO.getList()) {
			if (StringUtils.isBlank(distrOrderDetailDTO.getSkuDesc())) {
				log.warn("��С�������������");
				throw new RuntimeException("��С�������������");
			}
			if (StringUtils.isBlank(distrOrderDetailDTO.getSubjectName())) {
				log.warn("��Ʒ���Ʋ�����");
				throw new RuntimeException("��Ʒ���Ʋ�����");
			}
			if (distrOrderDetailDTO.getSkuCount() == null) {
				log.warn("��Ʒ����������");
				throw new RuntimeException("��Ʒ����������");
			}
			if (distrOrderDetailDTO.getSkuId() == null) {
				log.warn("��С���id������");
				throw new RuntimeException("��С���id������");
			}
			if (distrOrderDetailDTO.getSubjectId() == null) {
				log.warn("��Ʒid������");
				throw new RuntimeException("��Ʒid������");
			}
		}
		if (distrOrderDTO.getReceiverId() == null) {
			log.warn("���id������");
			throw new RuntimeException("���id������");
		}

		if (distrOrderDTO.getTradeOrderId() == null) {
			log.warn("����ϵͳ����id������");
			throw new RuntimeException("����ϵͳ����id������");
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
	
	//��鴫���DistrBatchOutOfStockResponseDTO
	private void checkDistrBatchOutOfStockResponseDTO(DistrBatchOutOfStockResponseDTO dbrDTO){
		if (dbrDTO == null){
			log.warn("�����DistrBatchOutOfStockResponseDTOΪ��");
			throw new RuntimeException("�����DistrBatchOutOfStockResponseDTOΪ��");
		}
		if (StringUtils.isBlank(dbrDTO.getStorageRef())){
			log.warn("��ˮ��Ϊ��");
			throw new RuntimeException("��ˮ��Ϊ��");
		}
		List<DistrBatchOutOfStockResponseDetail> list = dbrDTO.getList();
		if (list == null||list.isEmpty()){
			log.warn("ȱ��������ϸ�б�Ϊ��");
			throw new RuntimeException("ȱ��������ϸ�б�Ϊ��");
		}
		for (DistrBatchOutOfStockResponseDetail detail : list) {
			if (StringUtils.isBlank(detail.getPosLabel())){
				log.warn("��λ��Ϊ��");
				throw new RuntimeException("��λ��Ϊ��");
			}
			if (detail.getSkuCount() == null){
				log.warn("����Ϊ��");
				throw new RuntimeException("����Ϊ��");
			}
			if (detail.getSkuId() == null){
				log.warn("��С��浥ԪidΪ��");
				throw new RuntimeException("��С��浥ԪidΪ��");
			}
		}
	}
	
	// ���������α�
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
		log.info("�ɹ����������α�����idΪ{}", dbatch.getDistrBatchId());
		return dbatch.getDistrBatchId();
	}

	// �������޸ĵ����ϵͳ������
	private Integer updateDistrOrderBatch(DistrBatchOutOfStockResponseDTO dbrDTO, Integer operator) {
		checkDistrBatchOutOfStockResponseDTO(dbrDTO);
		checkInteger(operator);
		// ���������α��õ�����id
		Integer distrBatchId = saveToDistrBatch(dbrDTO, operator);
		if (distrBatchId == null) {
			log.warn("δ�ɹ���ӽ�������α�");
			throw new RuntimeException("δ�ɹ���ӽ�������α�");
		}
		DistrOrder order = new DistrOrder();
		order.setDistrStatus(DistrOrderStatus.WAIT_DISTRIBUTION);
		List<DistrOrder> list = distrOrderDao.findByExample(order);
		if (list == null || list.isEmpty()) {
			log.warn("û���ҵ��ȴ�����Ķ���");
			throw new RuntimeException("û���ҵ��ȴ�����Ķ���");
		}
		for (DistrOrder distrOrder : list) {
			distrOrder.setDistrBatchId(distrBatchId);
			// �޸����״̬
			distrOrder.setDistrStatus(DistrOrderStatus.PICKING_DISTRIBUTION);
			updateDistrOrder(distrOrder);
			log.info("�ɹ��޸��䷢ϵͳid={}��״̬Ϊ�������", distrOrder.getDistrOrderId());
		}
		return distrBatchId;
	}

	/**
	 * ���òִ��ӿڣ���ѯ��Ʒ�Ŀ�λ��Ϣ
	 * @param isAgain �Ƿ���β�ѯ�����β�ѯֻ�÷���֮ǰ�Ĳ�ѯ������������������
	 * @return
	 */
	private DistrBatchOutOfStockResponseDTO queryStorageInfo(List<DistrBatchRequestDTO> requestList, boolean isAgain) {
		// TODO ���ÿ��ӿ�,�Է��ؽ�����в����ж�
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

	// ����ȱ������ӿ�
	private DistrBatchOutOfStockResponseDTO getOutOfStockResponse(DistrBatchOutOfStockRequestDTO requestDTO) {
		// TODO ����ȱ������ӿڣ�������
		DistrBatchOutOfStockResponseDTO osrDTO = new DistrBatchOutOfStockResponseDTO();
		osrDTO.setStorageRef("QWER1002");
		osrDTO.setSuccess(false);
		osrDTO.setFailReason("��治��");

		List<DistrBatchOutOfStockResponseDetail> list = new ArrayList<DistrBatchOutOfStockResponseDetail>();
		DistrBatchOutOfStockResponseDetail osrDetail = new DistrBatchOutOfStockResponseDetail();
		osrDetail.setPosLabel("A001");
		osrDetail.setSkuCount(20);
		osrDetail.setSkuId(3);
		list.add(osrDetail);
		osrDTO.setList(list);
		return osrDTO;
	}
	
	//���distrOrderIdList
	private void checkIdList(List<Integer> distrOrderIdList){
		if (distrOrderIdList == null||distrOrderIdList.isEmpty()){
			log.warn("�����idListΪ��");
			throw new RuntimeException("�����idListΪ��");
		}
	}
	
	// �ִ���Ϣת��Ϊ����չʾ����Ϣ(�����Ʒ��Ϣ)
	private List<DistrBatchPrintDTO> transformStorageInfoToDisplayList(DistrBatchOutOfStockResponseDTO dbrDTO) {
		List<DistrBatchPrintDTO> printList = new ArrayList<DistrBatchPrintDTO>();
		for (DistrBatchOutOfStockResponseDetail responseDetail : dbrDTO.getList()) {
			DistrBatchPrintDTO printDTO = new DistrBatchPrintDTO();
			BeanUtils.copyProperties(responseDetail, printDTO);
			DistrOrderDetail detail = new DistrOrderDetail();
			Integer skuId = responseDetail.getSkuId();
			detail.setSkuId(skuId);
			// XXX �Ժ���ò�ѯ��Ʒģ��
			List<DistrOrderDetail> detailList = distrOrderDetailDao.findByExample(detail);
			if (detailList == null || detailList.isEmpty()){
				log.warn("������ϸ����skuIdΪ{}�ļ�¼������",skuId);
				throw new RuntimeException("������ϸ����skuIdΪ"+skuId+"�ļ�¼������");
			}
			printDTO.setSkuDesc(detailList.get(0).getSkuDesc());
			printDTO.setSubjectName(detailList.get(0).getSubjectName());
			printList.add(printDTO);
		}
		return printList;
	}
	
	/**
	 * ��ѯ�����-�������
	 * @param operator
	 * @param distrOrderIdList
	 * @return List<DistrBatchPrintDTO>
	 */
	public List<DistrBatchPrintDTO> findDistrOrderList(Integer operator,
			List<Integer> distrOrderIdList) {
		return findDistrOrderList(operator, distrOrderIdList, false);
	}

	private List<DistrBatchRequestDTO> getRequestList(List<Integer> distrOrderIdList) {
		// �������
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
	 * ��ѯ�����
	 * @param operator
	 * @param distrOrderIdList
	 * @return List<DistrBatchPrintDTO>
	 */
	public List<DistrBatchPrintDTO> findDistrOrderList(Integer operator,
			List<Integer> distrOrderIdList, boolean isAgain) {
		checkInteger(operator);
		checkIdList(distrOrderIdList);
		// XXX ��ѯ���ж���������ͬsku���������ϲ����Ա���òִ�ģ��ӿڽ��в�ѯ
		List<DistrBatchRequestDTO> requestList = getRequestList(distrOrderIdList);
		// ���òִ�ϵͳ�ӿڣ���ѯ��Ʒ���ڿ�λ��Ϣ
		DistrBatchOutOfStockResponseDTO dbrDTO = queryStorageInfo(requestList,isAgain);
		// �������޸ĵ����ϵͳ������
		if (!isAgain){
			Integer distrBatchId = updateDistrOrderBatch(dbrDTO, operator);
			log.info("�ɹ�����������Ϊ{}���䷢ϵͳ������", distrBatchId);
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
			log.warn("�����listΪ��");
			throw new RuntimeException("�����listΪ��");
		}
	}
	
	//ȱ�������������
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
	 * ȱ������
	 * @param outOfStockRequest
	 * @return List<DistrBatchPrintDTO>
	 */
	public List<DistrBatchPrintDTO> outOfStockOrderList(List<DistrBatchPrintDTO> printRequestList) {
		checkDistrBatchPrintDTO(printRequestList);
		DistrBatchOutOfStockRequestDTO requestDTO = getOutOfStockRequestDTO(printRequestList);
		// ��ѯ���ж���������ͬsku���������ϲ����Ա���òִ�ģ��ӿڽ��в�ѯ
		// ����storageȱ�����䣬������Ʒȱ����ѭ�����ýӿڣ����������󷵻�web��
		DistrBatchOutOfStockResponseDTO osrDTO = getOutOfStockResponse(requestDTO);
		return transformStorageInfoToDisplayList(osrDTO);
	}
	
	//���DistrExpressPreDTO
	private void checkDistrExpressPreDTO(DistrExpressPreDTO preDTO){
		if (preDTO == null){
			log.warn("�����DistrExpressPreDTOΪ��");
			throw new RuntimeException("�����DistrExpressPreDTOΪ��");
		}
		if (preDTO.getDistrOrderId() == null){
			log.warn("������䷢ϵͳ������Ϊ��");
			throw new RuntimeException("������䷢ϵͳ������Ϊ��");
		}
		if (StringUtils.isBlank(preDTO.getExpressNo())){
			log.warn("����Ŀ�ݵ���Ϊ��");
			throw new RuntimeException("����Ŀ�ݵ���Ϊ��");
		}
	}
	
	/**
	 * �����ݵ���
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
			log.warn("ϵͳ�����䷢���в����ڸ�����id={}", preDTO.getDistrOrderId());
			throw new RuntimeException("ϵͳ�����䷢���в�����idΪ"+preDTO.getDistrOrderId()+",����״̬��Ϊ�ѳ���Ķ���");
		}
		// ���ÿ�ݵ��ź��޸Ŀ�ݵ�״̬
		DistrOrder order = orderList.get(0);
		order.setExpressNo(preDTO.getExpressNo());
		order.setExpressStatus(ExpressStatus.WAITPRINT_EXPORT);
		updateDistrOrder(order);
		log.info("�����ݵ��ųɹ���������{}����ݵ���{}", order.getDistrOrderId(),
				order.getExpressNo());
	}
	
	/**
	 * ��ѯ����浥��Ϣ(��ӡ/���´�ӡ��ݵ�ʱ��ʹ��)
	 * 
	 * @param preDTO
	 * @return DistrExpressDTO
	 */
	public DistrExpressDTO queryExpressInfo(DistrExpressPreDTO preDTO) {
		checkDistrExpressPreDTO(preDTO);
		DistrOrder distrOrder = distrOrderDao
				.findById(preDTO.getDistrOrderId());
		if (distrOrder == null) {
			log.warn("�䷢ϵͳ����id={}������", preDTO.getDistrOrderId());
			throw new RuntimeException("ϵͳ�����䷢���в����ڸ�id"
					+ preDTO.getDistrOrderId());
		}
		// TODO �̼ҹ̶�����ϵ��ʽ
		DistrExpressDTO deDTO = new DistrExpressDTO();
		BeanUtils.copyProperties(distrOrder, deDTO);
		deDTO.setExpressNo(preDTO.getExpressNo());
		deDTO.setSenderAddr("�����人");
		deDTO.setSenderCode("13243");
		deDTO.setSenderName("����");
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
		log.info("��ѯ���ݵ��浥��Ϣ��ɹ����¿�ݵ�״̬Ϊ�Ѵ�ӡ������idΪ{}",preDTO.getDistrOrderId());
		return deDTO;
	}

	private void updateDistrOrder(DistrOrder order) {
		order.setGmtModify(DateFormat.getCurrentTime());
		order.setVersion(order.getVersion()+1);
		distrOrderDao.update(order);
	}

	//���String����
	private void checkString(String expressCompany){
		if (StringUtils.isBlank(expressCompany)){
			log.warn("�����String����Ϊ��");
			throw new RuntimeException("�����String����Ϊ��");
		}
	}
	
	/**
	 * �л���ݹ�˾
	 * 
	 * @param distrOrderId
	 * @param expressCompany
	 */
	public Integer changeExpressCompany(Integer distrOrderId, String expressCompany) {
		checkInteger(distrOrderId);
		checkString(expressCompany);
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		if (DistrOrderStatus.EXPORTING_DISTRIBUTION.equals(distrOrder.getDistrStatus())){
			log.warn("����idΪ{}�Ķ����ѳ��⣬�����л���ݹ�˾",distrOrderId);
			throw new RuntimeException("����idΪ"+distrOrderId+"�Ķ����ѳ��⣬�����л���ݹ�˾");
		}
		if (ExpressStatus.WRITTEN_EXPORT.equals(distrOrder.getExpressStatus())){
			log.warn("����idΪ{}�Ķ����Ѵ�ӡ�������л���ݹ�˾",distrOrderId);
			throw new RuntimeException("����idΪ"+distrOrderId+"�Ķ����Ѵ�ӡ�������л���ݹ�˾");
		}
		distrOrder.setExpressCompany(expressCompany);
		updateDistrOrder(distrOrder);
		log.info("�ɹ��޸��䷢ϵͳ����id={}�ļ�¼�Ŀ�ݹ�˾Ϊ{}", distrOrderId, expressCompany);
		String detail = "��ݹ�˾�л�Ϊ"+expressCompany;
		Integer changeId = saveToDistrChange(distrOrderId, detail,DistrChangeType.CHANGE_COMPANY);
		log.info("�ɹ���idΪ"+distrOrderId+"�Ķ�����ӵ������¼����");
		return changeId;
	}

	// ��鶩���Ƿ�ͨ��
	private CheckResultDTO checkIfOrderChanged(int distrOrderId) {
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		if(distrOrder == null) {
			return buildCheckResultDTO(false, "���������ڣ�distrOrderId=" + distrOrderId);
		}
		// ȡ���Ķ�����ͨ��
		if (DistrOrderStatus.ORDER_STATUS_CANCEL.equals(distrOrder.getDistrStatus())) {
			return buildCheckResultDTO(false, "�����ѱ�ȡ��");
		}
		// ���صĶ�����ͨ��
		if (DistrOrderStatus.BLOCK_DISTRIBUTION.equals(distrOrder.getDistrStatus())){
			return buildCheckResultDTO(false, "�����ѱ�����");
		}
		// ��������Ķ���(�ĵ�ַ/�Ŀ�ݹ�˾��)��ͨ��
		if (distrOrder.getChangeOrderFlag()) {
			return buildCheckResultDTO(false, "���������˱��");
		}
		return buildCheckResultDTO(true, "");
	}
	
	private CheckResultDTO buildCheckResultDTO(boolean isSuccess, String reason) {
		CheckResultDTO result = new CheckResultDTO();
		result.setSuccess(isSuccess);
		result.setResult(reason);
		return result;
	}

	// ͨ����ݵ��ż��
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

	// ͨ����ݵ��ŵõ�distrOrderId
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
			log.warn("��ݵ�Ϊ{}�Ŀ�ݵ��Ŷ�Ӧ��������",expressNo);
			throw new RuntimeException("��ݵ�Ϊ"+expressNo+"�Ŀ�ݵ��Ŷ�Ӧ��������");
		}
		return list.get(0);
	}

	//���DistrMapDTO
	private void checkDistrMapDTO(DistrMapDTO mapDTO){
		if (mapDTO == null){
			log.warn("�����DistrMapDTOΪ��");
			throw new RuntimeException("�����DistrMapDTOΪ��");
		}
	}
	/**
	 * �ּ���ɨ���ݵ��Ž��������session��
	 * 
	 * ��鶩��״̬�Ƿ�仯
	 * ����ַ�Ƿ�仯
	 * ����expressNo��skuId�����ּ�����ϵͳ��¼�Ƿ�һ��
	 * ͨ�����޸Ķ���״̬
	 * ����ResultDTO��������ͨ��ԭ��
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
		//������״̬Ϊ�ѳ��⣬�����쳣
		if (DistrOrderStatus.EXPORTING_DISTRIBUTION.equals(dorder.getDistrStatus())){
			log.warn("��ݵ���Ϊ{}�Ķ����ѳ���",expressNo);
			throw new RuntimeException("��ݵ���Ϊ"+expressNo+"�Ķ����ѳ���");
		}
		// ����Ƿ��ظ������״̬��ɨ��
		if (DistrOrderStatus.CHECKOK_DISTRIBUTION.equals(dorder.getDistrStatus())) {
			log.warn("��ݵ���Ϊ{}�ļ�¼�ѱ��ɹ�ɨ���", expressNo);
			throw new RuntimeException("��ɨ��ɹ����ö�������ݵ���Ϊ" + expressNo);
		}
		List<DistrOrderDetail> detailList = findDistrOrderDetailByDistrOrderId(distrOrderId);
		if (detailList == null||detailList.isEmpty()){
			log.warn("�䷢ϵͳ��ϸ���в������䷢ϵͳidΪ{}����ϸ",distrOrderId);
			throw new RuntimeException("�䷢ϵͳ��ϸ���в������䷢ϵͳidΪ"+distrOrderId+"����ϸ");
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
	 * ���ּ�����ϵͳ��¼�Ƿ�һ��
	 * @param goodDTO
	 * @param mapDTO
	 * @return CheckResultDTO
	 */
	public CheckResultDTO checkGoodsMatch(DistrGoodsDTO goodDTO,
			DistrMapDTO mapDTO) {
		checkDistrMapDTO(mapDTO);
		if (goodDTO.getDistrOrderId() == null){
			throw new RuntimeException("������䷢ϵͳ����idΪ��");
		}
		if (goodDTO.getSkuCount() == null){
			throw new RuntimeException("�������Ʒ����Ϊ��");
		}
		if (StringUtils.isBlank(goodDTO.getSkuDesc())){
			throw new RuntimeException("�������С��Ԫ����Ϊ��");
		}
		CheckResultDTO result = new CheckResultDTO();
		Map<Integer,DistrGoodsDTO> comparedMap = mapDTO.getComparedMap();
		Map<Integer, DistrGoodsDTO> scannedMap = mapDTO.getScannedMap();

		// ��mapDTO��ȡ��scannedEntry��Ȼ������+1
		DistrGoodsDTO scannedEntry = getScannedEntryFromMap(scannedMap,
				goodDTO.getSkuId());
		scannedEntry.setSkuCount(scannedEntry.getSkuCount() + 1);

		int key = goodDTO.getSkuId();
		DistrGoodsDTO dbEntry = comparedMap.get(key);
		if (dbEntry == null) {
			result.setSuccess(false);
			result.setResult("ɨ����ﲻ��ϵͳ������");
			return result;
		}

		if (scannedEntry.getSkuCount() < dbEntry.getSkuCount()) {
			result.setSuccess(true);
			result.setResult("ɨ������С��ϵͳ����");
			return result;
		}
		if (scannedEntry.getSkuCount() > dbEntry.getSkuCount()) {
			result.setSuccess(false);
			result.setResult("ɨ����������ϵͳ����");
			return result;
		}
		// ɨ������ == ϵͳ����
		for (Integer comparedKey : comparedMap.keySet()) {
			DistrGoodsDTO goodsDTO = scannedMap.get(comparedKey);
			if (goodsDTO == null){
				result.setSuccess(true);
				result.setResult("ϵͳ��¼�е��������ﻹδ��ɨ��");
				return result;
			}
			if (goodsDTO.getSkuCount() != comparedMap.get(comparedKey).getSkuCount()) {
				result.setSuccess(true);
				result.setResult("ɨ����������ϵͳ����,����δɨ���������ƥ��");
				return result;
			}
		}
		int distrOrderId = dbEntry.getDistrOrderId();
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		distrOrder.setDistrStatus(DistrOrderStatus.CHECKOK_DISTRIBUTION);
		updateDistrOrder(distrOrder);
		log.info("�ɹ�ɨ��ͨ������Ʒ����С��浥ԪidΪ{}", goodDTO.getSkuId());
		result.setSuccess(true);
		result.setResult("�ÿ�ݵ���ϵͳƥ��һ��");
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

	// ���⴦��
	private void sendOutByDistrOrderId(int distrOrderId) {
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		if (!DistrOrderStatus.CHECKOK_DISTRIBUTION.equals(distrOrder.getDistrStatus())
				&& !DistrOrderStatus.PICKING_DISTRIBUTION.equals(distrOrder.getDistrStatus())) {
			log.warn("����idΪ{}�Ķ���δ����ɹ������ܳ���", distrOrderId);
			throw new RuntimeException("����idΪ" + distrOrderId + "�Ķ���δ����ɹ������ܳ���");
		}
		distrOrder.setDistrStatus(DistrOrderStatus.EXPORTING_DISTRIBUTION);
		updateDistrOrder(distrOrder);
		log.info("�ɹ��޸��䷢ϵͳ����idΪ{}�ļ�¼���䷢״̬Ϊ�ѳ���", distrOrderId);
		// TODO ����tradebase֪ͨ���׷���
	}

	/**
	 * ���⣬ͨ����ݵ���
	 * 
	 * ��鶩��״̬�Ƿ�仯
	 * ����ַ�Ƿ�仯
	 * ͨ�����޸Ķ���״̬
	 * ����tradebase֪ͨ���׷���
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
	 * ���⣬ͨ������id
	 * 
	 * @param distrOrderId
	 * @return CheckResultDTO
	 */
	public void sendOutByDistrOrderId(List<Integer> distrOrderIds) {
		checkIdList(distrOrderIds);
		if (distrOrderIds == null || distrOrderIds.isEmpty()) {
			log.warn("�䷢ϵͳ����idΪ��");
			throw new RuntimeException("�䷢ϵͳ����idΪ��");
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
	 * ȡ��������
	 * 
	 * @param distrOrderId
	 */
	public void cancelDistr(int distrOrderId) {
		checkInteger(distrOrderId);
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		if (DistrOrderStatus.EXPORTING_DISTRIBUTION.equals(distrOrder
				.getDistrStatus())) {
			log.warn("�䷢ϵͳ������idΪ{}�Ķ����ѳ���", distrOrderId);
			throw new RuntimeException("�ö����ѳ���, distrOrderId=" + distrOrderId);
		}
		if (DistrOrderStatus.ORDER_STATUS_CANCEL.equals(distrOrder
				.getDistrStatus())) {
			return;
		}
		distrOrder.setDistrStatus(DistrOrderStatus.ORDER_STATUS_CANCEL);
		updateDistrOrder(distrOrder);
		log.info("�ɹ��޸��䷢ϵͳ����idΪ{}�Ķ���״̬Ϊ��ȡ��", distrOrderId);
		String detail = "����Ϊ"+distrOrderId+"�Ķ����ѱ�ȡ��";
		saveToDistrChange(distrOrderId, detail,DistrChangeType.ORDER_CANCELED);
		log.info("����Ϊ{}�Ķ����ѱ�ȡ��",distrOrderId);
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
