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
	
	//取消拦截订单(原状态为已拦截)
	@Test
	public void unblockOrder(){
		//准备数据
		DistrOrderDTO distrOrderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(distrOrderDTO);
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		distrOrder.setPeblockStatus(distrOrder.getDistrStatus());
		distrOrder.setDistrStatus(DistrOrderStatus.BLOCK_DISTRIBUTION);
		distrOrderDao.update(distrOrder);
		//执行
		Integer changeId = distrService.unblockOrder(distrOrderDTO.getTradeOrderId(),"");
		//检验
		Assert.assertEquals(distrOrderDao.findById(distrOrderId).getPeblockStatus(), distrOrderDao.findById(distrOrderId).getDistrStatus());
		Assert.assertEquals(DistrChangeType.ORDER_UNBLOCKED, distrChangeDao.findById(changeId).getType());
	}
	//取消拦截订单(原状态不为已拦截)
	@Test(expected=RuntimeException.class)
	public void unblockOrder2(){
		//准备数据
		DistrOrderDTO distrOrderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(distrOrderDTO);
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		distrOrder.setPeblockStatus(distrOrder.getDistrStatus());
		distrOrderDao.update(distrOrder);
		//执行
		distrService.unblockOrder(distrOrderDTO.getTradeOrderId(),"");
		//检验
		Assert.assertEquals(distrOrderDao.findById(distrOrderId).getPeblockStatus(), distrOrderDao.findById(distrOrderId).getDistrStatus());
	}
	//拦截订单
	@Test
	public void blockOrder(){
		//准备数据
		DistrOrderDTO distrOrderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(distrOrderDTO);
		//执行
		int changeId = distrService.blockOrder(distrOrderDTO.getTradeOrderId(),"");
		//验证
		Assert.assertEquals(DistrOrderStatus.BLOCK_DISTRIBUTION, distrOrderDao.findById(distrOrderId).getDistrStatus());
		Assert.assertEquals(DistrChangeType.ORDER_BLOCKED, distrChangeDao.findById(changeId).getType());		
	}
	//切换快递公司
	@Test
	public void changeCompany(){
		//准备数据
		DistrOrderDTO distrOrderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(distrOrderDTO);
		String expressCompany = "expressCompany"+addToDistrOrderInterfTest.getRandomOrderId();
		//执行
		int changeId = distrService.changeOrderExpressCompany(distrOrderDTO.getTradeOrderId(), expressCompany);
		//检验
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		Assert.assertEquals(true, distrOrder.getChangeOrderFlag());
		Assert.assertEquals(expressCompany, distrOrder.getExpressCompany());
		Assert.assertEquals(DistrChangeType.CHANGE_COMPANY, distrChangeDao.findById(changeId).getType());
	}
	//取消订单
	@Test
	public void cancelOrder(){
		//准备数据
		DistrOrderDTO distrOrderDTO = addToDistrOrderInterfTest.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(distrOrderDTO);
		//执行
		distrService.cancelOrder(distrOrderDTO.getTradeOrderId(), "");
		//检验
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

	// 修改地址
	@Test
	public void changeAddr() {
		// 准备数据
		DistrOrderDTO distrOrderDTO = addToDistrOrderInterfTest
				.buildDistrOrderDTO();
		Integer distrOrderId = distrService.saveDistrOrder(distrOrderDTO);
		DistrOrderAddrDTO addrDTO = new DistrOrderAddrDTO();
		addrDTO.setReceiveAddr("addr"+addToDistrOrderInterfTest.getRandomOrderId());
		addrDTO.setReceiveCityId(addToDistrOrderInterfTest.getRandomOrderId());
		addrDTO.setReceiveCode("code"+addToDistrOrderInterfTest.getRandomOrderId());
		addrDTO.setReceiveName("name"+addToDistrOrderInterfTest.getRandomOrderId());
		addrDTO.setReceivePhoneNo("no"+addToDistrOrderInterfTest.getRandomOrderId());
		// 执行
		Integer changeId = distrService.changeOrderAddr(distrOrderDTO.getTradeOrderId(), addrDTO);
		// 检验
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
