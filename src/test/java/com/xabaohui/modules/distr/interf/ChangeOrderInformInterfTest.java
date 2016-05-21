package com.xabaohui.modules.distr.interf;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.xabaohui.modules.distr.bean.DistrChange;
import com.xabaohui.modules.distr.bean.DistrChangeType;
import com.xabaohui.modules.distr.bean.DistrOrder;
import com.xabaohui.modules.distr.bean.DistrOrderRelation;
import com.xabaohui.modules.distr.bean.DistrOrderStatus;
import com.xabaohui.modules.distr.dao.DistrChangeDao;
import com.xabaohui.modules.distr.dao.DistrOrderDao;
import com.xabaohui.modules.distr.dao.DistrOrderRelationDao;
import com.xabaohui.modules.distr.dto.DistrChangeDTO;
import com.xabaohui.modules.distr.dto.DistrOrderAddrDTO;
import com.xabaohui.modules.distr.dto.DistrOrderDTO;
import com.xabaohui.modules.distr.service.DistrService;
@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=false)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:applicationContext.xml")
@Component
public class ChangeOrderInformInterfTest extends AbstractJUnit4SpringContextTests{
	@Resource
	private DistrService distrService;
	@Resource
	private AddToDistrOrderInterfTest addToDistrOrderInterfTest;
	@Resource
	private DistrOrderRelationDao relationDao;
	@Resource
	private DistrOrderDao distrOrderDao;
	@Resource
	private DistrChangeDao distrChangeDao;
	
	//ȡ�����ض���(ԭ״̬Ϊ������)
	@Test
	public void unblockOrder(){
		//׼������
		DistrOrderDTO distrOrderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(distrOrderDTO);
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		distrOrder.setPeblockStatus(distrOrder.getDistrStatus());
		distrOrder.setDistrStatus(DistrOrderStatus.BLOCK_DISTRIBUTION);
		distrOrderDao.update(distrOrder);
		//ִ��
		Integer changeId = distrService.unblockOrder(distrOrderDTO.getTradeOrderId(),"");
		//����
		Assert.assertEquals(distrOrderDao.findById(distrOrderId).getPeblockStatus(), distrOrderDao.findById(distrOrderId).getDistrStatus());
		Assert.assertEquals(DistrChangeType.ORDER_UNBLOCKED, distrChangeDao.findById(changeId).getType());
	}
	//ȡ�����ض���(ԭ״̬��Ϊ������)
	@Test(expected=RuntimeException.class)
	public void unblockOrder2(){
		//׼������
		DistrOrderDTO distrOrderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(distrOrderDTO);
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		distrOrder.setPeblockStatus(distrOrder.getDistrStatus());
		distrOrderDao.update(distrOrder);
		//ִ��
		distrService.unblockOrder(distrOrderDTO.getTradeOrderId(),"");
		//����
		Assert.assertEquals(distrOrderDao.findById(distrOrderId).getPeblockStatus(), distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//���ض���
	@Test
	public void blockOrder(){
		//׼������
		DistrOrderDTO distrOrderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(distrOrderDTO);
		//ִ��
		int changeId = distrService.blockOrder(distrOrderDTO.getTradeOrderId(),"");
		//��֤
		Assert.assertEquals(DistrOrderStatus.BLOCK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		Assert.assertEquals(DistrChangeType.ORDER_BLOCKED, distrChangeDao.findById(changeId).getType());		
	}
	//�л���ݹ�˾
	@Test
	public void changeCompany(){
		//׼������
		DistrOrderDTO distrOrderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(distrOrderDTO);
		String expressCompany = "expressCompany"+addToDistrOrderInterfTest.getRandomOrderId();
		//ִ��
		int changeId = distrService.changeOrderExpressCompany(distrOrderDTO.getTradeOrderId(), expressCompany);
		//����
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		Assert.assertEquals(true, distrOrder.getChangeOrderFlag());
		Assert.assertEquals(expressCompany, distrOrder.getExpressCompany());
		Assert.assertEquals(DistrChangeType.CHANGE_COMPANY, distrChangeDao.findById(changeId).getType());
	}
	//ȡ������
	@Test
	public void cancelOrder(){
		//׼������
		DistrOrderDTO distrOrderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(distrOrderDTO);
		//ִ��
		distrService.cancelOrder(distrOrderDTO.getTradeOrderId(), "");
		//����
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

	// �޸ĵ�ַ
	@Test
	public void changeAddr() {
		// ׼������
		DistrOrderDTO distrOrderDTO = addToDistrOrderInterfTest
				.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(distrOrderDTO);
		DistrOrderAddrDTO addrDTO = new DistrOrderAddrDTO();
		addrDTO.setReceiveAddr("addr"+addToDistrOrderInterfTest.getRandomOrderId());
		addrDTO.setReceiveCityId(addToDistrOrderInterfTest.getRandomOrderId());
		addrDTO.setReceiveCode("code"+addToDistrOrderInterfTest.getRandomOrderId());
		addrDTO.setReceiveName("name"+addToDistrOrderInterfTest.getRandomOrderId());
		addrDTO.setReceivePhoneNo("no"+addToDistrOrderInterfTest.getRandomOrderId());
		// ִ��
		Integer changeId = distrService.changeOrderAddr(distrOrderDTO.getTradeOrderId(), addrDTO);
		// ����
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		Assert.assertEquals(true,distrOrder.getChangeOrderFlag());
		Assert.assertEquals(DistrChangeType.CHANGE_ADDR, distrChangeDao.findById(changeId).getType());
		Assert.assertEquals(addrDTO.getReceiveAddr(), distrOrder.getReceiveAddr());
		Assert.assertEquals(addrDTO.getReceiveCode(), distrOrder.getReceiveCode());
		Assert.assertEquals(addrDTO.getReceiveName(), distrOrder.getReceiveName());
		Assert.assertEquals(addrDTO.getReceivePhoneNo(), distrOrder.getReceivePhoneNo());
		Assert.assertEquals(addrDTO.getReceiveCityId(), distrOrder.getReceiveCityId());
	}
}
