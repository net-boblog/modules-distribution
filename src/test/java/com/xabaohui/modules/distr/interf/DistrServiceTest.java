package com.xabaohui.modules.distr.interf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.xabaohui.modules.distr.bean.DistrChange;
import com.xabaohui.modules.distr.bean.DistrChangeType;
import com.xabaohui.modules.distr.bean.DistrOrder;
import com.xabaohui.modules.distr.bean.DistrOrderDetail;
import com.xabaohui.modules.distr.bean.DistrOrderStatus;
import com.xabaohui.modules.distr.bean.ExpressStatus;
import com.xabaohui.modules.distr.dao.DistrBatchDao;
import com.xabaohui.modules.distr.dao.DistrChangeDao;
import com.xabaohui.modules.distr.dao.DistrOrderDao;
import com.xabaohui.modules.distr.dao.DistrOrderDetailDao;
import com.xabaohui.modules.distr.dto.CheckResultDTO;
import com.xabaohui.modules.distr.dto.DistrBatchPrintDTO;
import com.xabaohui.modules.distr.dto.DistrExpressDTO;
import com.xabaohui.modules.distr.dto.DistrExpressPreDTO;
import com.xabaohui.modules.distr.dto.DistrGoodsDTO;
import com.xabaohui.modules.distr.dto.DistrMapDTO;
import com.xabaohui.modules.distr.dto.DistrOrderDTO;
import com.xabaohui.modules.distr.dto.DistrOrderDetailDTO;
import com.xabaohui.modules.distr.service.DistrService;
@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=false)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:applicationContext.xml")
@Component
public class DistrServiceTest extends AbstractJUnit4SpringContextTests{
	@Resource
	private AddToDistrOrderInterfTest addToDistrOrderInterfTest;
	@Resource
	private DistrService distrService;
	@Resource
	private DistrOrderDao distrOrderDao;
	@Resource
	private DistrBatchDao distrBatchDao;
	@Resource
	private DistrOrderDetailDao distrOrderDetailDao;
	@Resource
	private DistrChangeDao distrChangeDao;
	//初次配货
	@Test
	public void printDistrOrder() {
		// 准备数据
		List<Integer> list = new ArrayList<Integer>();
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(orderDTO);
		list.add(distrOrderId);
		DistrOrderDTO orderDTO2 = addToDistrOrderInterfTest
				.buildDistrOrderDTO();
		Integer distrOrderId2 = distrService.saveDistrOrder(orderDTO2);
		list.add(distrOrderId2);
		// 执行
		List<DistrBatchPrintDTO> printList = distrService.findDistrOrderList(
				addToDistrOrderInterfTest.getRandomOrderId(), list);
		// 验证结果
		for (int i = 0; i < list.size(); i++) {
			DistrOrder distrOrder = distrOrderDao.findById((Integer) list
					.get(i));
			Assert.assertEquals(DistrOrderStatus.PICKING_DISTRIBUTION,
					distrOrder.getDistrStatus());
			Assert.assertNotNull(distrOrder.getDistrBatchId());
			Assert.assertNotNull(distrBatchDao.findById(
					distrOrder.getDistrBatchId()).getStorageRef());
		}
		for (DistrBatchPrintDTO distrBatchPrintDTO : printList) {
			Assert.assertNotNull(distrBatchPrintDTO.getPosLabel());
			Assert.assertNotNull(distrBatchPrintDTO.getSkuDesc());
			Assert.assertNotNull(distrBatchPrintDTO.getSubjectName());
			Assert.assertNotNull(distrBatchPrintDTO.getSkuCount());
		}
	}
	//重新打印配货单
	@Test
	public void printDistrOrderAgain(){
		//准备数据
		List<Integer> list = new ArrayList<Integer>();
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(orderDTO);
		list.add(distrOrderId);
		DistrOrderDTO orderDTO2 = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId2 = distrService.saveDistrOrder(orderDTO2);
		list.add(distrOrderId2);
		distrService.findDistrOrderList(
				addToDistrOrderInterfTest.getRandomOrderId(), list);
		//执行
		List<DistrBatchPrintDTO> printList = distrService.findDistrOrderList(addToDistrOrderInterfTest.getRandomOrderId(),list,true);
		//验证结果
		for (int i=0;i<list.size();i++){
			DistrOrder distrOrder = distrOrderDao.findById((Integer)list.get(i));
			Assert.assertEquals(DistrOrderStatus.PICKING_DISTRIBUTION, distrOrder.getDistrStatus());
			Assert.assertNotNull(distrOrder.getDistrBatchId());
			Assert.assertNotNull(distrBatchDao.findById(distrOrder.getDistrBatchId()).getStorageRef());
		}
		for (DistrBatchPrintDTO distrBatchPrintDTO : printList) {
			Assert.assertNotNull(distrBatchPrintDTO.getPosLabel());
			Assert.assertNotNull(distrBatchPrintDTO.getSkuDesc());
			Assert.assertNotNull(distrBatchPrintDTO.getSubjectName());
			Assert.assertNotNull(distrBatchPrintDTO.getSkuCount());
		}
	}
	//缺货补配
	/*@Test
	public void printOutOfStockOrder(){
		List<DistrBatchPrintDTO> printList = distrService.outOfStockOrderList(null);
		for (DistrBatchPrintDTO distrBatchPrintDTO : printList) {
			Assert.assertNotNull(distrBatchPrintDTO.getPosLabel());
			Assert.assertNotNull(distrBatchPrintDTO.getSkuDesc());
			Assert.assertNotNull(distrBatchPrintDTO.getSubjectName());
			Assert.assertNotNull(distrBatchPrintDTO.getSkuCount());
		}
	}*/
	//存快递单号
	@Test
	public void saveExpressNo(){
		//准备数据
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(orderDTO);
		DistrExpressPreDTO expressDTO = new DistrExpressPreDTO();
		expressDTO.setDistrOrderId(distrOrderId);
		expressDTO.setExpressNo("1"+addToDistrOrderInterfTest.getRandomOrderId());
		//执行
		distrService.saveExpressNo(expressDTO);
		//验证结果
		DistrOrder distrOrder = distrOrderDao.findById(expressDTO.getDistrOrderId());
		Assert.assertEquals(expressDTO.getExpressNo(), distrOrder.getExpressNo());
	}
	//打印快递单
	@Test
	public void printDistrExpress(){
		//准备数据
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(orderDTO);
		DistrExpressPreDTO expressDTO = new DistrExpressPreDTO();
		expressDTO.setDistrOrderId(distrOrderId);
		expressDTO.setExpressNo("1"+addToDistrOrderInterfTest.getRandomOrderId());
		
		//执行
		distrService.saveExpressNo(expressDTO);
		DistrExpressDTO distrExpressDTO = distrService.queryExpressInfo(expressDTO);
		
		//验证结果
		DistrOrder distrOrder = distrOrderDao.findById(expressDTO.getDistrOrderId());
		Assert.assertEquals(expressDTO.getExpressNo(), distrOrder.getExpressNo());
		Assert.assertEquals(ExpressStatus.WRITTEN_EXPORT, distrOrder.getExpressStatus());
		Assert.assertEquals(expressDTO.getExpressNo(), distrExpressDTO.getExpressNo());
		Assert.assertNotNull(distrExpressDTO.getReceiveAddr());
		Assert.assertNotNull(distrExpressDTO.getReceiveCode());
		Assert.assertNotNull(distrExpressDTO.getReceiveName());
		Assert.assertNotNull(distrExpressDTO.getReceivePhoneNo());
		Assert.assertNotNull(distrExpressDTO.getSenderAddr());
		Assert.assertNotNull(distrExpressDTO.getSenderPhoneNo());
		Assert.assertNotNull(distrExpressDTO.getSenderCode());
		Assert.assertNotNull(distrExpressDTO.getSenderName());
		Assert.assertNotNull(distrExpressDTO.getList());
	}
	//切换快递公司
	@Test
	public void changeExpressCompany(){
		//准备数据
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(orderDTO);
		String expressCompany="2"+addToDistrOrderInterfTest.getRandomOrderId();
		//执行
		int changeId = distrService.changeExpressCompany(distrOrderId, expressCompany);
		//验证
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		Assert.assertEquals(expressCompany, distrOrder.getExpressCompany());
		Assert.assertEquals(DistrChangeType.CHANGE_COMPANY, distrChangeDao.findById(changeId).getType());
	}
	@Test(expected=RuntimeException.class)
	public void changeExpressCompany2(){
		//准备数据
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(orderDTO);
		DistrOrder order = distrOrderDao.findById(distrOrderId);
		order.setDistrStatus(DistrOrderStatus.EXPORTING_DISTRIBUTION);
		distrOrderDao.update(order);
		String expressCompany="2"+addToDistrOrderInterfTest.getRandomOrderId();
		//执行
		distrService.changeExpressCompany(distrOrderId, expressCompany);
		//验证
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		Assert.assertEquals(expressCompany, distrOrder.getExpressCompany());
	}
	//扫描快递单号
	@Test
	public void checkExpressNo(){
		//准备数据
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		int distrOrderId = distrService.saveDistrOrder(orderDTO);
		String expressNo="3"+addToDistrOrderInterfTest.getRandomOrderId();
		DistrExpressPreDTO expressDTO = new DistrExpressPreDTO();
		expressDTO.setDistrOrderId(distrOrderId);
		expressDTO.setExpressNo(expressNo);
		distrService.saveExpressNo(expressDTO);
		DistrMapDTO mapDTO = new DistrMapDTO();
		
		//执行
		CheckResultDTO result = distrService.checkExpressNo(expressNo,mapDTO);
		//验证
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("", result.getResult());
	}
	//扫描货物是否匹配    A1--A3
	@Test
	public void checkGoodsMatch1(){
		//准备数据
		DistrMapDTO mapDTO = prepareOne();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		
		
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		DistrOrderDetail detail = list.get(0);
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(1);
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//验证
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("扫描数量小于系统数量", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//扫描货物是否匹配    A3--A3
	@Test
	public void checkGoodsMatch2(){
		//准备数据
		DistrMapDTO mapDTO = prepareOne();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		DistrOrderDetail detail = distrOrderDetailDao.findById(list.get(0).getDistrOrderDetailId());
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(29);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//验证
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("该快递单与系统匹配一致", result.getResult());
		Assert.assertEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//c1
	private Integer saveC1(){
		DistrOrderDTO orderDTO = buildDistrOrderDTO();
		int distrOrderId = distrService.saveDistrOrder(orderDTO);
		String expressNo="3"+addToDistrOrderInterfTest.getRandomOrderId();
		DistrExpressPreDTO expressDTO = new DistrExpressPreDTO();
		expressDTO.setDistrOrderId(distrOrderId);
		expressDTO.setExpressNo(expressNo);
		distrService.saveExpressNo(expressDTO);
		return distrOrderId;
	}
	private DistrOrderDTO buildDistrOrderDTO() {
		DistrOrderDTO distrOrderDTO = new DistrOrderDTO();
		distrOrderDTO.setReceiveCityId(4);
		distrOrderDTO.setExpressCompany("expressCompany");
		distrOrderDTO.setTradeOrderId(addToDistrOrderInterfTest.getRandomOrderId());
		distrOrderDTO.setReceiveAddr("湖北武汉4");
		distrOrderDTO.setReceiveCode("7411233");
		distrOrderDTO.setReceiveName("张三");
		distrOrderDTO.setReceivePhoneNo("1"+addToDistrOrderInterfTest.getRandomOrderId());
		distrOrderDTO.setReceiverId(5);
		distrOrderDTO.setStatus(DistrOrderStatus.WAIT_DISTRIBUTION);
		List<DistrOrderDetailDTO> detailList = new ArrayList<DistrOrderDetailDTO>();
		DistrOrderDetailDTO detail = new DistrOrderDetailDTO();
		detail.setSkuId(6);
		detail.setSkuCount(30);
		detail.setSkuDesc("黄色42码");
		detail.setSubjectId(5);
		detail.setSubjectName("匹克跑鞋");
		detailList.add(detail);
		distrOrderDTO.setList(detailList);
		return distrOrderDTO;
	}
	//扫描货物是否匹配    A1,c1--A3
	@Test
	public void checkGoodsMatch3(){
		//准备数据
		DistrMapDTO mapDTO = prepareOne();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(10);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		
		int distrOrderId2 = saveC1();
		
		orderDetail.setDistrOrderId(distrOrderId2);
		List<DistrOrderDetail> list2 = distrOrderDetailDao.findByExample(orderDetail);
		if (list2 == null || list2.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId2+"id没有对应的明细");
		}
		DistrOrderDetail detail2 = list2.get(0);
		DistrGoodsDTO goodsDTO2 = new DistrGoodsDTO();
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(10);
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//验证
		Assert.assertEquals(false, result.isSuccess());
		Assert.assertEquals("扫描货物不在系统订单中", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//扫描货物是否匹配    A3,c1--A3
	@Test
	public void checkGoodsMatch4(){
		//准备数据
		DistrMapDTO mapDTO = prepareOne();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(30);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		
		int distrOrderId2 = saveC1();
		
		DistrGoodsDTO goodsDTO2 = new DistrGoodsDTO();
		orderDetail.setDistrOrderId(distrOrderId2);
		List<DistrOrderDetail> list2 = distrOrderDetailDao.findByExample(orderDetail);
		if (list2 == null || list2.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId2+"id没有对应的明细");
		}
		DistrOrderDetail detail2 = list2.get(0);
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(10);
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//验证
		Assert.assertEquals(false, result.isSuccess());
		Assert.assertEquals("扫描货物不在系统订单中", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	private DistrOrderDTO buildTwoDetailDTO() {
		DistrOrderDTO distrOrderDTO = new DistrOrderDTO();
		distrOrderDTO.setReceiveCityId(3);
		distrOrderDTO.setExpressCompany("expressCompany"+addToDistrOrderInterfTest.getRandomOrderId());
		distrOrderDTO.setTradeOrderId(addToDistrOrderInterfTest.getRandomOrderId());
		distrOrderDTO.setReceiveAddr("湖北武汉4");
		distrOrderDTO.setReceiveCode("7411233");
		distrOrderDTO.setReceiveName("张三");
		distrOrderDTO.setReceivePhoneNo("1"+addToDistrOrderInterfTest.getRandomOrderId());
		distrOrderDTO.setReceiverId(5);
		List<DistrOrderDetailDTO> detailList = new ArrayList<DistrOrderDetailDTO>();
		DistrOrderDetailDTO detail = new DistrOrderDetailDTO();
		detail.setSkuId(4);
		detail.setSkuCount(30);
		detail.setSkuDesc("黄色42码");
		detail.setSubjectId(5);
		detail.setSubjectName("匹克跑鞋");
		detailList.add(detail);
		DistrOrderDetailDTO detail2 = new DistrOrderDetailDTO();
		detail2.setSkuId(5);
		detail2.setSkuCount(30);
		detail2.setSkuDesc("黄色42码");
		detail2.setSubjectId(6);
		detail2.setSubjectName("匹克跑鞋");
		detailList.add(detail2);
		distrOrderDTO.setList(detailList);
		return distrOrderDTO;
	}
	//扫描货物是否匹配    A1--A3,B3
	@Test
	public void checkGoodsMatch5(){
		//准备数据
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(1);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//验证
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("扫描数量小于系统数量", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//扫描货物是否匹配    A3--A3,B3
	@Test
	public void checkGoodsMatch6(){
		//准备数据
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(29);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//验证
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("系统记录中的其他货物还未被扫描", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//准备数据通用
	private DistrMapDTO prepare(DistrOrderDTO orderDTO){
		int distrOrderId = distrService.saveDistrOrder(orderDTO);
		String expressNo="3"+addToDistrOrderInterfTest.getRandomOrderId();
		DistrExpressPreDTO expressDTO = new DistrExpressPreDTO();
		expressDTO.setDistrOrderId(distrOrderId);
		expressDTO.setExpressNo(expressNo);
		distrService.saveExpressNo(expressDTO);
		DistrMapDTO mapDTO = new DistrMapDTO();
		distrService.checkExpressNo(expressNo, mapDTO);
		mapDTO.setDistrOrderId(distrOrderId);
		return mapDTO;
	}
	private DistrMapDTO prepareTwo(){
		DistrOrderDTO orderDTO = buildTwoDetailDTO();
		return prepare(orderDTO);
	}
	private DistrMapDTO prepareOne(){
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		return prepare(orderDTO);
	}
	//扫描货物是否匹配    A4--A3,B3
	@Test
	public void checkGoodsMatch7(){
		//准备数据
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(30);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//验证
		Assert.assertEquals(false, result.isSuccess());
		Assert.assertEquals("扫描数量超过系统数量", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//扫描货物是否匹配    A4,B1--A3,B3
	@Test
	public void checkGoodsMatch8(){
		//准备数据
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		//A4
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		DistrOrderDetail detail = distrOrderDetailDao.findById(list.get(0).getDistrOrderDetailId());
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(30);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//B1
		DistrGoodsDTO goodsDTO2 = new DistrGoodsDTO();
		DistrOrderDetail detail2 = list.get(1);
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(10);
		mapDTO.getScannedMap().put(goodsDTO2.getSkuId(), goodsDTO2);
		
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//验证
		Assert.assertEquals(false, result.isSuccess());
		Assert.assertEquals("扫描数量超过系统数量", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//扫描货物是否匹配    A3,B1--A3,B3
	@Test
	public void checkGoodsMatch9(){
		//准备数据
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		//A3
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(29);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//B1
		DistrGoodsDTO goodsDTO2 = new DistrGoodsDTO();
		DistrOrderDetail detail2 = list.get(1);
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(0);
		
		//全部扫描A
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//验证
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("系统记录中的其他货物还未被扫描", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		//第一次扫B
		//执行
		CheckResultDTO result2 = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//验证
		Assert.assertEquals(true, result2.isSuccess());
		Assert.assertEquals("扫描数量小于系统数量", result2.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		
		mapDTO.getScannedMap().put(goodsDTO2.getSkuId(), goodsDTO2);
	}
	//扫描货物是否匹配    A3,B3--A3,B3
	@Test
	public void checkGoodsMatch10(){
		//准备数据
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		//A3
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(29);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//B3
		DistrGoodsDTO goodsDTO2 = new DistrGoodsDTO();
		DistrOrderDetail detail2 = list.get(1);
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(29);
		mapDTO.getScannedMap().put(goodsDTO2.getSkuId(), goodsDTO2);
		//先扫完A
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//验证
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("扫描数量等于系统数量,其他未扫描的数量不匹配", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		//再扫B
		//执行
		CheckResultDTO result2 = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//验证
		Assert.assertEquals(true, result2.isSuccess());
		Assert.assertEquals("该快递单与系统匹配一致", result2.getResult());
		Assert.assertEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		}
	//扫描货物是否匹配    A4,B3--A3,B3
	@Test
	public void checkGoodsMatch11(){
		//准备数据
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		//B未完全扫完之前先扫完A
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(30);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//B3
		DistrGoodsDTO goodsDTO2 = new DistrGoodsDTO();
		DistrOrderDetail detail2 = list.get(1);
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(29);
		mapDTO.getScannedMap().put(goodsDTO2.getSkuId(), goodsDTO2);
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//验证
		Assert.assertEquals(false, result.isSuccess());
		Assert.assertEquals("扫描数量超过系统数量", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		}
	//扫描货物是否匹配    A1,c1--A3,B3
	@Test
	public void checkGoodsMatch12(){
		//准备数据
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(10);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//C1
		int distrOrderId2 = saveC1();
		DistrGoodsDTO goodsDTO2 = new DistrGoodsDTO();
		orderDetail.setDistrOrderId(distrOrderId2);
		List<DistrOrderDetail> list2 = distrOrderDetailDao.findByExample(orderDetail);
		if (list2 == null || list2.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId2+"id没有对应的明细");
		}
		DistrOrderDetail detail2 = list2.get(0);
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(10);
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//验证
		Assert.assertEquals(false, result.isSuccess());
		Assert.assertEquals("扫描货物不在系统订单中", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		}
	//扫描货物是否匹配    A3,c1--A3,B3
	@Test
	public void checkGoodsMatch13(){
		//准备数据
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(29);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//C1
		int distrOrderId2 = saveC1();
		DistrGoodsDTO goodsDTO2 = new DistrGoodsDTO();
		orderDetail.setDistrOrderId(distrOrderId2);
		List<DistrOrderDetail> list2 = distrOrderDetailDao.findByExample(orderDetail);
		if (list2 == null || list2.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId2+"id没有对应的明细");
		}
		DistrOrderDetail detail2 = list2.get(0);
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(10);
		//扫A最后一个
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//验证
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("系统记录中的其他货物还未被扫描", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		//A扫完然后扫C
		//执行
		CheckResultDTO result2 = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//验证
		Assert.assertEquals(false, result2.isSuccess());
		Assert.assertEquals("扫描货物不在系统订单中", result2.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId2).getDistrStatus());
		}
	//扫描货物是否匹配    A1,B1,c1--A3,B3
	@Test
	public void checkGoodsMatch14(){
		//准备数据
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		//A1
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(10);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//B1
		DistrOrderDetail detail3 = list.get(1);
		BeanUtils.copyProperties(detail3, goodsDTO);
		goodsDTO.setSkuCount(0);
		//C1
		int distrOrderId2 = saveC1();
		DistrGoodsDTO goodsDTO2 = new DistrGoodsDTO();
		orderDetail.setDistrOrderId(distrOrderId2);
		List<DistrOrderDetail> list2 = distrOrderDetailDao.findByExample(orderDetail);
		if (list2 == null || list2.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId2+"id没有对应的明细");
		}
		DistrOrderDetail detail2 = distrOrderDetailDao.findById(list2.get(0).getDistrOrderDetailId());
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(0);
		//A扫了部分然后扫B
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//验证
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("扫描数量小于系统数量", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		//然后再扫C
		//执行
		CheckResultDTO result2 = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//验证
		Assert.assertEquals(false, result2.isSuccess());
		Assert.assertEquals("扫描货物不在系统订单中", result2.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId2).getDistrStatus());
		}
	//扫描货物是否匹配    A3,B1,c1--A3,B3
	@Test
	public void checkGoodsMatch15(){
		//准备数据
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId+"id没有对应的明细");
		}
		//A3
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(29);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//B1
		DistrGoodsDTO goodsDTO3 = new DistrGoodsDTO();
		DistrOrderDetail detail3 = list.get(1);
		BeanUtils.copyProperties(detail3, goodsDTO3);
		goodsDTO3.setSkuCount(0);
		//C1
		int distrOrderId2 = saveC1();
		DistrGoodsDTO goodsDTO2 = new DistrGoodsDTO();
		orderDetail.setDistrOrderId(distrOrderId2);
		List<DistrOrderDetail> list2 = distrOrderDetailDao.findByExample(orderDetail);
		if (list2 == null || list2.isEmpty()){
			throw new RuntimeException("该配发系统订单："+distrOrderId2+"id没有对应的明细");
		}
		DistrOrderDetail detail2 = list2.get(0);
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(0);
		//先扫完A
		//执行
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//验证
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("系统记录中的其他货物还未被扫描", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		//然后再扫部分B
		//执行
		CheckResultDTO result2 = distrService.checkGoodsMatch(goodsDTO3, mapDTO);
		//验证
		Assert.assertEquals(true, result2.isSuccess());
		Assert.assertEquals("扫描数量小于系统数量", result2.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId2).getDistrStatus());
		//然后再扫C
		//执行
		CheckResultDTO result3 = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//验证
		Assert.assertEquals(false, result3.isSuccess());
		Assert.assertEquals("扫描货物不在系统订单中", result3.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId2).getDistrStatus());
		}
	//出库，通过快递单号
	@Test
	public void sendOutByExpressNo(){
		//准备数据
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		int distrOrderId = distrService.saveDistrOrder(orderDTO);
		List<Integer> list = new ArrayList<Integer>();
		list.add(distrOrderId);
		distrService.findDistrOrderList(addToDistrOrderInterfTest.getRandomOrderId(), list);
		String expressNo = "1"+addToDistrOrderInterfTest.getRandomOrderId();
		DistrExpressPreDTO expressDTO = new DistrExpressPreDTO();
		expressDTO.setDistrOrderId(distrOrderId);
		expressDTO.setExpressNo(expressNo);
		distrService.saveExpressNo(expressDTO);
		distrService.queryExpressInfo(expressDTO);
		// 生成订单（随机ID）->配货->打快递单("express"+orderId)->出库
		//执行
		CheckResultDTO result = distrService.sendOutByExpressNo(expressNo);
		//验证
		DistrOrder distrOrder = new DistrOrder();
		distrOrder.setExpressNo(expressNo);
		List<DistrOrder> doList = distrOrderDao.findByExample(distrOrder);
		if (doList == null || doList.isEmpty()){
			throw new RuntimeException("该快递单号不存在");
		}
		if (doList.size()>1){
			throw new RuntimeException("数据库中有多条快递单号为"+expressNo+"的数据");
		}
		Assert.assertEquals(DistrOrderStatus.EXPORTING_DISTRIBUTION, doList.get(0).getDistrStatus());
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("", result.getResult());
	}
	//出库，通过配发系统订单id
	@Test
	public void sendOutByDistrOrderId(){
		//准备数据
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		int distrOrderId = distrService.saveDistrOrder(orderDTO);
		List<Integer> list = new ArrayList<Integer>();
		list.add(distrOrderId);
		distrService.findDistrOrderList(addToDistrOrderInterfTest.getRandomOrderId(), list);
		String expressNo = "1"+addToDistrOrderInterfTest.getRandomOrderId();
		DistrExpressPreDTO expressDTO = new DistrExpressPreDTO();
		expressDTO.setDistrOrderId(distrOrderId);
		expressDTO.setExpressNo(expressNo);
		distrService.saveExpressNo(expressDTO);
		distrService.queryExpressInfo(expressDTO);
		//执行
		distrService.sendOutByDistrOrderId(list);
		//验证
		Assert.assertEquals(DistrOrderStatus.EXPORTING_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	private Integer getRandomDetailId() {
		Integer id = null;
		do {
			int randomId = (int) (Math.random() * 200);
			System.out.println(randomId);
			DistrOrderDetail detail = distrOrderDetailDao.findById(randomId);
			if (detail == null) {
				id = randomId;
			}
		} while (id == null);
		return id;
	}
	//取消发货单
	@Test
	public void cancelDistr(){
		//准备数据
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		int distrOrderId = distrService.saveDistrOrder(orderDTO);
		//执行
		distrService.cancelDistr(distrOrderId);
		//验证
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		Assert.assertEquals(DistrOrderStatus.ORDER_STATUS_CANCEL, distrOrder.getDistrStatus());
		
		DistrChange change = new DistrChange();
		change.setDistrOrderId(distrOrderId);
		List<DistrChange> changeList = distrChangeDao.findByExample(change);
		if (changeList == null || changeList.isEmpty()){
			throw new RuntimeException("订单id为"+distrOrderId+"的订单没有变更记录");
		}
		String type = "";
		for (DistrChange distrChange : changeList) {
			type = distrChange.getType();
		}
		Assert.assertEquals(DistrChangeType.ORDER_CANCELED, type);
	}
}
