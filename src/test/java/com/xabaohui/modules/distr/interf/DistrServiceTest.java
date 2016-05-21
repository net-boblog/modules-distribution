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
	//�������
	@Test
	public void printDistrOrder() {
		// ׼������
		List<Integer> list = new ArrayList<Integer>();
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(orderDTO);
		list.add(distrOrderId);
		DistrOrderDTO orderDTO2 = addToDistrOrderInterfTest
				.buildDistrOrderDTO();
		Integer distrOrderId2 = distrService.saveDistrOrder(orderDTO2);
		list.add(distrOrderId2);
		// ִ��
		List<DistrBatchPrintDTO> printList = distrService.findDistrOrderList(
				addToDistrOrderInterfTest.getRandomOrderId(), list);
		// ��֤���
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
	//���´�ӡ�����
	@Test
	public void printDistrOrderAgain(){
		//׼������
		List<Integer> list = new ArrayList<Integer>();
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(orderDTO);
		list.add(distrOrderId);
		DistrOrderDTO orderDTO2 = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId2 = distrService.saveDistrOrder(orderDTO2);
		list.add(distrOrderId2);
		distrService.findDistrOrderList(
				addToDistrOrderInterfTest.getRandomOrderId(), list);
		//ִ��
		List<DistrBatchPrintDTO> printList = distrService.findDistrOrderList(addToDistrOrderInterfTest.getRandomOrderId(),list,true);
		//��֤���
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
	//ȱ������
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
	//���ݵ���
	@Test
	public void saveExpressNo(){
		//׼������
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(orderDTO);
		DistrExpressPreDTO expressDTO = new DistrExpressPreDTO();
		expressDTO.setDistrOrderId(distrOrderId);
		expressDTO.setExpressNo("1"+addToDistrOrderInterfTest.getRandomOrderId());
		//ִ��
		distrService.saveExpressNo(expressDTO);
		//��֤���
		DistrOrder distrOrder = distrOrderDao.findById(expressDTO.getDistrOrderId());
		Assert.assertEquals(expressDTO.getExpressNo(), distrOrder.getExpressNo());
	}
	//��ӡ��ݵ�
	@Test
	public void printDistrExpress(){
		//׼������
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(orderDTO);
		DistrExpressPreDTO expressDTO = new DistrExpressPreDTO();
		expressDTO.setDistrOrderId(distrOrderId);
		expressDTO.setExpressNo("1"+addToDistrOrderInterfTest.getRandomOrderId());
		
		//ִ��
		distrService.saveExpressNo(expressDTO);
		DistrExpressDTO distrExpressDTO = distrService.queryExpressInfo(expressDTO);
		
		//��֤���
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
	//�л���ݹ�˾
	@Test
	public void changeExpressCompany(){
		//׼������
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(orderDTO);
		String expressCompany="2"+addToDistrOrderInterfTest.getRandomOrderId();
		//ִ��
		int changeId = distrService.changeExpressCompany(distrOrderId, expressCompany);
		//��֤
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		Assert.assertEquals(expressCompany, distrOrder.getExpressCompany());
		Assert.assertEquals(DistrChangeType.CHANGE_COMPANY, distrChangeDao.findById(changeId).getType());
	}
	@Test(expected=RuntimeException.class)
	public void changeExpressCompany2(){
		//׼������
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(orderDTO);
		DistrOrder order = distrOrderDao.findById(distrOrderId);
		order.setDistrStatus(DistrOrderStatus.EXPORTING_DISTRIBUTION);
		distrOrderDao.update(order);
		String expressCompany="2"+addToDistrOrderInterfTest.getRandomOrderId();
		//ִ��
		distrService.changeExpressCompany(distrOrderId, expressCompany);
		//��֤
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		Assert.assertEquals(expressCompany, distrOrder.getExpressCompany());
	}
	//ɨ���ݵ���
	@Test
	public void checkExpressNo(){
		//׼������
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		int distrOrderId = distrService.saveDistrOrder(orderDTO);
		String expressNo="3"+addToDistrOrderInterfTest.getRandomOrderId();
		DistrExpressPreDTO expressDTO = new DistrExpressPreDTO();
		expressDTO.setDistrOrderId(distrOrderId);
		expressDTO.setExpressNo(expressNo);
		distrService.saveExpressNo(expressDTO);
		DistrMapDTO mapDTO = new DistrMapDTO();
		
		//ִ��
		CheckResultDTO result = distrService.checkExpressNo(expressNo,mapDTO);
		//��֤
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("", result.getResult());
	}
	//ɨ������Ƿ�ƥ��    A1--A3
	@Test
	public void checkGoodsMatch1(){
		//׼������
		DistrMapDTO mapDTO = prepareOne();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		
		
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
		}
		DistrOrderDetail detail = list.get(0);
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(1);
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//��֤
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("ɨ������С��ϵͳ����", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//ɨ������Ƿ�ƥ��    A3--A3
	@Test
	public void checkGoodsMatch2(){
		//׼������
		DistrMapDTO mapDTO = prepareOne();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
		}
		DistrOrderDetail detail = distrOrderDetailDao.findById(list.get(0).getDistrOrderDetailId());
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(29);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//��֤
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("�ÿ�ݵ���ϵͳƥ��һ��", result.getResult());
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
		distrOrderDTO.setReceiveAddr("�����人4");
		distrOrderDTO.setReceiveCode("7411233");
		distrOrderDTO.setReceiveName("����");
		distrOrderDTO.setReceivePhoneNo("1"+addToDistrOrderInterfTest.getRandomOrderId());
		distrOrderDTO.setReceiverId(5);
		distrOrderDTO.setStatus(DistrOrderStatus.WAIT_DISTRIBUTION);
		List<DistrOrderDetailDTO> detailList = new ArrayList<DistrOrderDetailDTO>();
		DistrOrderDetailDTO detail = new DistrOrderDetailDTO();
		detail.setSkuId(6);
		detail.setSkuCount(30);
		detail.setSkuDesc("��ɫ42��");
		detail.setSubjectId(5);
		detail.setSubjectName("ƥ����Ь");
		detailList.add(detail);
		distrOrderDTO.setList(detailList);
		return distrOrderDTO;
	}
	//ɨ������Ƿ�ƥ��    A1,c1--A3
	@Test
	public void checkGoodsMatch3(){
		//׼������
		DistrMapDTO mapDTO = prepareOne();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(10);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		
		int distrOrderId2 = saveC1();
		
		orderDetail.setDistrOrderId(distrOrderId2);
		List<DistrOrderDetail> list2 = distrOrderDetailDao.findByExample(orderDetail);
		if (list2 == null || list2.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId2+"idû�ж�Ӧ����ϸ");
		}
		DistrOrderDetail detail2 = list2.get(0);
		DistrGoodsDTO goodsDTO2 = new DistrGoodsDTO();
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(10);
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//��֤
		Assert.assertEquals(false, result.isSuccess());
		Assert.assertEquals("ɨ����ﲻ��ϵͳ������", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//ɨ������Ƿ�ƥ��    A3,c1--A3
	@Test
	public void checkGoodsMatch4(){
		//׼������
		DistrMapDTO mapDTO = prepareOne();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
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
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId2+"idû�ж�Ӧ����ϸ");
		}
		DistrOrderDetail detail2 = list2.get(0);
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(10);
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//��֤
		Assert.assertEquals(false, result.isSuccess());
		Assert.assertEquals("ɨ����ﲻ��ϵͳ������", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	private DistrOrderDTO buildTwoDetailDTO() {
		DistrOrderDTO distrOrderDTO = new DistrOrderDTO();
		distrOrderDTO.setReceiveCityId(3);
		distrOrderDTO.setExpressCompany("expressCompany"+addToDistrOrderInterfTest.getRandomOrderId());
		distrOrderDTO.setTradeOrderId(addToDistrOrderInterfTest.getRandomOrderId());
		distrOrderDTO.setReceiveAddr("�����人4");
		distrOrderDTO.setReceiveCode("7411233");
		distrOrderDTO.setReceiveName("����");
		distrOrderDTO.setReceivePhoneNo("1"+addToDistrOrderInterfTest.getRandomOrderId());
		distrOrderDTO.setReceiverId(5);
		List<DistrOrderDetailDTO> detailList = new ArrayList<DistrOrderDetailDTO>();
		DistrOrderDetailDTO detail = new DistrOrderDetailDTO();
		detail.setSkuId(4);
		detail.setSkuCount(30);
		detail.setSkuDesc("��ɫ42��");
		detail.setSubjectId(5);
		detail.setSubjectName("ƥ����Ь");
		detailList.add(detail);
		DistrOrderDetailDTO detail2 = new DistrOrderDetailDTO();
		detail2.setSkuId(5);
		detail2.setSkuCount(30);
		detail2.setSkuDesc("��ɫ42��");
		detail2.setSubjectId(6);
		detail2.setSubjectName("ƥ����Ь");
		detailList.add(detail2);
		distrOrderDTO.setList(detailList);
		return distrOrderDTO;
	}
	//ɨ������Ƿ�ƥ��    A1--A3,B3
	@Test
	public void checkGoodsMatch5(){
		//׼������
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(1);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//��֤
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("ɨ������С��ϵͳ����", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//ɨ������Ƿ�ƥ��    A3--A3,B3
	@Test
	public void checkGoodsMatch6(){
		//׼������
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(29);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//��֤
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("ϵͳ��¼�е��������ﻹδ��ɨ��", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//׼������ͨ��
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
	//ɨ������Ƿ�ƥ��    A4--A3,B3
	@Test
	public void checkGoodsMatch7(){
		//׼������
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
		}
		DistrOrderDetail detail = list.get(0);
		BeanUtils.copyProperties(detail, goodsDTO);
		goodsDTO.setSkuCount(30);
		mapDTO.getScannedMap().put(goodsDTO.getSkuId(), goodsDTO);
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//��֤
		Assert.assertEquals(false, result.isSuccess());
		Assert.assertEquals("ɨ����������ϵͳ����", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//ɨ������Ƿ�ƥ��    A4,B1--A3,B3
	@Test
	public void checkGoodsMatch8(){
		//׼������
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		//A4
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
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
		
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//��֤
		Assert.assertEquals(false, result.isSuccess());
		Assert.assertEquals("ɨ����������ϵͳ����", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//ɨ������Ƿ�ƥ��    A3,B1--A3,B3
	@Test
	public void checkGoodsMatch9(){
		//׼������
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		//A3
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
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
		
		//ȫ��ɨ��A
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//��֤
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("ϵͳ��¼�е��������ﻹδ��ɨ��", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		//��һ��ɨB
		//ִ��
		CheckResultDTO result2 = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//��֤
		Assert.assertEquals(true, result2.isSuccess());
		Assert.assertEquals("ɨ������С��ϵͳ����", result2.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		
		mapDTO.getScannedMap().put(goodsDTO2.getSkuId(), goodsDTO2);
	}
	//ɨ������Ƿ�ƥ��    A3,B3--A3,B3
	@Test
	public void checkGoodsMatch10(){
		//׼������
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		//A3
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
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
		//��ɨ��A
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//��֤
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("ɨ����������ϵͳ����,����δɨ���������ƥ��", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		//��ɨB
		//ִ��
		CheckResultDTO result2 = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//��֤
		Assert.assertEquals(true, result2.isSuccess());
		Assert.assertEquals("�ÿ�ݵ���ϵͳƥ��һ��", result2.getResult());
		Assert.assertEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		}
	//ɨ������Ƿ�ƥ��    A4,B3--A3,B3
	@Test
	public void checkGoodsMatch11(){
		//׼������
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		//Bδ��ȫɨ��֮ǰ��ɨ��A
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
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
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//��֤
		Assert.assertEquals(false, result.isSuccess());
		Assert.assertEquals("ɨ����������ϵͳ����", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		}
	//ɨ������Ƿ�ƥ��    A1,c1--A3,B3
	@Test
	public void checkGoodsMatch12(){
		//׼������
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
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
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId2+"idû�ж�Ӧ����ϸ");
		}
		DistrOrderDetail detail2 = list2.get(0);
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(10);
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//��֤
		Assert.assertEquals(false, result.isSuccess());
		Assert.assertEquals("ɨ����ﲻ��ϵͳ������", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		}
	//ɨ������Ƿ�ƥ��    A3,c1--A3,B3
	@Test
	public void checkGoodsMatch13(){
		//׼������
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
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
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId2+"idû�ж�Ӧ����ϸ");
		}
		DistrOrderDetail detail2 = list2.get(0);
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(10);
		//ɨA���һ��
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//��֤
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("ϵͳ��¼�е��������ﻹδ��ɨ��", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		//Aɨ��Ȼ��ɨC
		//ִ��
		CheckResultDTO result2 = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//��֤
		Assert.assertEquals(false, result2.isSuccess());
		Assert.assertEquals("ɨ����ﲻ��ϵͳ������", result2.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId2).getDistrStatus());
		}
	//ɨ������Ƿ�ƥ��    A1,B1,c1--A3,B3
	@Test
	public void checkGoodsMatch14(){
		//׼������
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
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
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId2+"idû�ж�Ӧ����ϸ");
		}
		DistrOrderDetail detail2 = distrOrderDetailDao.findById(list2.get(0).getDistrOrderDetailId());
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(0);
		//Aɨ�˲���Ȼ��ɨB
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//��֤
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("ɨ������С��ϵͳ����", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		//Ȼ����ɨC
		//ִ��
		CheckResultDTO result2 = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//��֤
		Assert.assertEquals(false, result2.isSuccess());
		Assert.assertEquals("ɨ����ﲻ��ϵͳ������", result2.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId2).getDistrStatus());
		}
	//ɨ������Ƿ�ƥ��    A3,B1,c1--A3,B3
	@Test
	public void checkGoodsMatch15(){
		//׼������
		DistrMapDTO mapDTO = prepareTwo();
		Integer distrOrderId = mapDTO.getDistrOrderId();
		DistrGoodsDTO goodsDTO = new DistrGoodsDTO();
		DistrOrderDetail orderDetail = new DistrOrderDetail();
		orderDetail.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> list = distrOrderDetailDao.findByExample(orderDetail);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId+"idû�ж�Ӧ����ϸ");
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
			throw new RuntimeException("���䷢ϵͳ������"+distrOrderId2+"idû�ж�Ӧ����ϸ");
		}
		DistrOrderDetail detail2 = list2.get(0);
		BeanUtils.copyProperties(detail2, goodsDTO2);
		goodsDTO2.setSkuCount(0);
		//��ɨ��A
		//ִ��
		CheckResultDTO result = distrService.checkGoodsMatch(goodsDTO, mapDTO);
		//��֤
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("ϵͳ��¼�е��������ﻹδ��ɨ��", result.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		//Ȼ����ɨ����B
		//ִ��
		CheckResultDTO result2 = distrService.checkGoodsMatch(goodsDTO3, mapDTO);
		//��֤
		Assert.assertEquals(true, result2.isSuccess());
		Assert.assertEquals("ɨ������С��ϵͳ����", result2.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId2).getDistrStatus());
		//Ȼ����ɨC
		//ִ��
		CheckResultDTO result3 = distrService.checkGoodsMatch(goodsDTO2, mapDTO);
		//��֤
		Assert.assertEquals(false, result3.isSuccess());
		Assert.assertEquals("ɨ����ﲻ��ϵͳ������", result3.getResult());
		Assert.assertNotEquals(DistrOrderStatus.CHECKOK_DISTRIBUTION, distrOrderDao.findById(distrOrderId2).getDistrStatus());
		}
	//���⣬ͨ����ݵ���
	@Test
	public void sendOutByExpressNo(){
		//׼������
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
		// ���ɶ��������ID��->���->���ݵ�("express"+orderId)->����
		//ִ��
		CheckResultDTO result = distrService.sendOutByExpressNo(expressNo);
		//��֤
		DistrOrder distrOrder = new DistrOrder();
		distrOrder.setExpressNo(expressNo);
		List<DistrOrder> doList = distrOrderDao.findByExample(distrOrder);
		if (doList == null || doList.isEmpty()){
			throw new RuntimeException("�ÿ�ݵ��Ų�����");
		}
		if (doList.size()>1){
			throw new RuntimeException("���ݿ����ж�����ݵ���Ϊ"+expressNo+"������");
		}
		Assert.assertEquals(DistrOrderStatus.EXPORTING_DISTRIBUTION, doList.get(0).getDistrStatus());
		Assert.assertEquals(true, result.isSuccess());
		Assert.assertEquals("", result.getResult());
	}
	//���⣬ͨ���䷢ϵͳ����id
	@Test
	public void sendOutByDistrOrderId(){
		//׼������
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
		//ִ��
		distrService.sendOutByDistrOrderId(list);
		//��֤
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
	//ȡ��������
	@Test
	public void cancelDistr(){
		//׼������
		DistrOrderDTO orderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		int distrOrderId = distrService.saveDistrOrder(orderDTO);
		//ִ��
		distrService.cancelDistr(distrOrderId);
		//��֤
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		Assert.assertEquals(DistrOrderStatus.ORDER_STATUS_CANCEL, distrOrder.getDistrStatus());
		
		DistrChange change = new DistrChange();
		change.setDistrOrderId(distrOrderId);
		List<DistrChange> changeList = distrChangeDao.findByExample(change);
		if (changeList == null || changeList.isEmpty()){
			throw new RuntimeException("����idΪ"+distrOrderId+"�Ķ���û�б����¼");
		}
		String type = "";
		for (DistrChange distrChange : changeList) {
			type = distrChange.getType();
		}
		Assert.assertEquals(DistrChangeType.ORDER_CANCELED, type);
	}
}
