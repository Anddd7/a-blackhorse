### Table of Content
- [In Scope](#in-scope)
- [Out of Scope](#out-of-scope)
- [AC 1 收到处罚信息，余额扣减](#ac-1)
  - [示例 1-1 当前商户id：10001，账户余额为100；订单id：aaaa-bbbb-cccc-dddd，处罚扣减100；更新后账户id：10001，账户余额为0，订单收入记录增加扣罚信息](#example-1-1)
  - [示例 1-2 当前商户id：10001，账户余额为100；订单id：aaaa-bbbb-cccc-dddd，处罚扣减90；更新后账户id：10001，账户余额为10，订单收入记录增加扣罚信息](#example-1-2)
- [AC 2 收到处罚信息，余额为0时，扣减](#ac-2)
  - [示例 2-1 当前商户id：10001，账户余额为100，押金30000；订单id：aaaa-bbbb-cccc-dddd，处罚扣减110；更新后账户id：10001，账户余额为0，押金29990，订单收入记录增加扣罚信息](#example-2-1)
  - [示例 2-2 当前商户id：10001，账户余额为100，押金10；订单id：aaaa-bbbb-cccc-dddd，处罚扣减110；更新后账户id：10001，账户余额为0，押金0，订单收入记录增加扣罚信息](#example-2-2)
  - [示例 2-3 当前商户id：10001，账户余额为100，押金0；订单id：aaaa-bbbb-cccc-dddd，处罚扣减110；更新后账户id：10001，账户余额为0，押金-10，订单收入记录增加扣罚信息](#example-2-3)
- [API Schema](#api-schema)
# Story-1002
### In Scope
作为 【平台运营】，我想要 【根据合作协议，扣除商家的余额入账】，以便于【作为处罚】

业务规则：
- 当余额扣减为0时，会扣减押金
- 需要返回扣减结果给下游服务（"订单管理应用服务"），以便调用平台通知告知商家
- 押金可为负数（押金<=0时会对商家进行停业整顿，并进行押金补交）
- 扣减记录需要追溯（允许开具发票）

涉及的数据结构：（可参考API Schema）
- 商家账户包含（id，余额，押金）
- 收入记录包含（商家id，订单id，收入金额，扣罚金额，扣罚原因）
### Out of Scope
假设：此接口由下游服务 "订单管理应用服务" 在订单完成后自动调用，返回200时即为确认，返回500时下游服务会自动重试
假设：押金<=0时会对商家进行停业整顿，由下游服务对交易系统进行操作控制
### <span id='ac-1'>AC 1 </span>
收到处罚信息，余额扣减
#### <span id='example-1-1'>示例 1-1 当前商户id：10001，账户余额为100；订单id：aaaa-bbbb-cccc-dddd，处罚扣减100；更新后账户id：10001，账户余额为0，订单收入记录增加扣罚信息</span>
##### 任务列表
 - **工序 1-1 | Mock<MerchantService.Service> | 15 mins**
 
	按示例组装ViewObject，mock Service正常执行
	按示例发送Http请求，进行余额扣罚
	调用成功，返回更新后的余额
	```
	API Call:
	> POST /merchant-account/balance/deduct
	< 200 OK
	```
 
----
 - **工序 1-2 | Mock<MerchantService.RepositoryClient> | 30 mins**
 
	按示例组装ViewObject
	    - mock Repository返回当前账户Entity（账户余额100）
	    - mock Repository保存当前账户Entity（账户余额0）
	    - mock Repository返回收入记录Entity（收入100，扣罚0）
	    - mock Repository保存收入记录Entity（收入100，扣罚100，原因投诉）
	调用Service方法，进行余额扣罚
	调用成功，返回更新后的余额（0）
 
----
 - **工序 1-4 | Fake<MerchantService.DB> | 30 mins**
 
	按示例组装收入记录信息Entity，能够通过fake DB进行保存和查询
 
----
##### 时序图
![2877904b-ce06-41e0-a890-739aec5e6f52](temp/story-1002/2877904b-ce06-41e0-a890-739aec5e6f52.svg)
#### <span id='example-1-2'>示例 1-2 当前商户id：10001，账户余额为100；订单id：aaaa-bbbb-cccc-dddd，处罚扣减90；更新后账户id：10001，账户余额为10，订单收入记录增加扣罚信息</span>
##### 任务列表
 - **工序 1-2 | Mock<MerchantService.RepositoryClient> | 30 mins**
 
	按示例组装ViewObject
	    - mock Repository返回当前账户Entity（账户余额100）
	    - mock Repository保存当前账户Entity（账户余额0）
	    - mock Repository返回收入记录Entity（收入100，扣罚0）
	    - mock Repository保存收入记录Entity（收入100，扣罚100，原因投诉）
	调用Service方法，进行余额扣罚
	调用成功，返回更新后的余额（10）
 
----
##### 时序图
![627bfcc2-90d8-4e6b-8d34-fbfdcbc43a55](temp/story-1002/627bfcc2-90d8-4e6b-8d34-fbfdcbc43a55.svg)
### <span id='ac-2'>AC 2 </span>
收到处罚信息，余额为0时，扣减
#### <span id='example-2-1'>示例 2-1 当前商户id：10001，账户余额为100，押金30000；订单id：aaaa-bbbb-cccc-dddd，处罚扣减110；更新后账户id：10001，账户余额为0，押金29990，订单收入记录增加扣罚信息</span>
##### 任务列表
 - **工序 1-1 | Mock<MerchantService.Service> | 15 mins**
 
	按示例组装ViewObject，mock Service正常执行
	按示例发送Http请求，进行余额扣罚
	调用成功，返回更新后的余额和押金
	```
	API Call:
	> POST /merchant-account/balance/deduct
	< 200 OK
	```
 
----
 - **工序 1-2 | Mock<MerchantService.RepositoryClient> | 30 mins**
 
	按示例组装ViewObject
	    - mock Repository返回当前账户Entity（账户余额100，押金30000）
	    - mock Repository保存当前账户Entity（账户余额0，押金29990）
	    - mock Repository返回收入记录Entity（收入100，扣罚110）
	    - mock Repository保存收入记录Entity（收入100，扣罚110，原因投诉）
	调用Service方法，进行余额扣罚
	调用成功，返回更新后的余额和押金（0，29990）
 
----
 - **工序 1-4 | Fake<MerchantService.DB> | 30 mins**
 
	押金能随账户余额被更新，能够通过fake DB进行保存和查询
	按示例组装收入记录信息Entity，能够通过fake DB进行保存和查询
 
----
##### 时序图
![cd1630a6-4f53-45eb-b4b9-ba8a71c74f19](temp/story-1002/cd1630a6-4f53-45eb-b4b9-ba8a71c74f19.svg)
#### <span id='example-2-2'>示例 2-2 当前商户id：10001，账户余额为100，押金10；订单id：aaaa-bbbb-cccc-dddd，处罚扣减110；更新后账户id：10001，账户余额为0，押金0，订单收入记录增加扣罚信息</span>
##### 任务列表
 - **工序 1-2 | Mock<MerchantService.RepositoryClient> | 30 mins**
 
	按示例组装ViewObject
	    - mock Repository返回当前账户Entity（账户余额100，押金10）
	    - mock Repository保存当前账户Entity（账户余额0，押金0）
	    - mock Repository返回收入记录Entity（收入100，扣罚110）
	    - mock Repository保存收入记录Entity（收入100，扣罚110，原因投诉）
	调用Service方法，进行余额扣罚
	调用成功，返回更新后的余额和押金（0，0）
 
----
##### 时序图
![9b89a616-156a-4b04-b9ac-c831afcec5cc](temp/story-1002/9b89a616-156a-4b04-b9ac-c831afcec5cc.svg)
#### <span id='example-2-3'>示例 2-3 当前商户id：10001，账户余额为100，押金0；订单id：aaaa-bbbb-cccc-dddd，处罚扣减110；更新后账户id：10001，账户余额为0，押金-10，订单收入记录增加扣罚信息</span>
##### 任务列表
 - **工序 1-2 | Mock<MerchantService.RepositoryClient> | 30 mins**
 
	按示例组装ViewObject
	    - mock Repository返回当前账户Entity（账户余额100，押金0）
	    - mock Repository保存当前账户Entity（账户余额0，押金-10）
	    - mock Repository返回收入记录Entity（收入100，扣罚110）
	    - mock Repository保存收入记录Entity（收入100，扣罚110，原因投诉）
	调用Service方法，进行余额扣罚
	调用成功，返回更新后的余额和押金（0，-10）
 
----
##### 时序图
![399024fb-77f1-4b8d-b8de-6ec96881d13d](temp/story-1002/399024fb-77f1-4b8d-b8de-6ec96881d13d.svg)
### API Schema
#### 订单处罚API
> POST /merchant-account/balance/deduct
- 200 OK
  - Request
  ```json
  {
      "merchant_account_id": 100001,
      "order_id": "<uuid>",
      "deduct_amount": 100
  }
  ```
  - Response
  ```json
  {
      "balance": 100,
      "deposit": 100
  }
  ```
### 进程内架构设计
### MerchantService
餐品订购服务: 为商家提供接入平台的服务，包括开通账号、缴纳押金、提现入账余额、收据和发票开具的功能；平台可对违反合作协议的商家进行押金扣减、入账扣减
Tech Stack: **[Spring Boot, PostgreSQL]**
 
![0827ed08-c5a8-456a-986e-58c1f9c2acad](temp/story-1002/0827ed08-c5a8-456a-986e-58c1f9c2acad.svg)
#### 工序拆分
##### 工序 1-1 | Controller => Mock\<Service>
实现Controller获取Http请求参数，调用Service并获取ViewObject，再返回序列化的Json数据
##### 工序 1-2 | Service => Mock\<RepositoryClient>
实现Service调用Client获取DTO，组装成ViewObject并返回
##### 工序 1-3 | RepositoryClient => Mock\<MQGateway>
实现Client调用MQ，通过DTO映射请求和返回的Json数据，验证发送和接收的数据正确
##### 工序 1-4 | RepositoryClient => Fake\<DB>
实现Repository调用DB，通过Entity映射数据库表，验证JPA的配置正确、数据库表创建正确、SQL语句书写正确
##### 工序 1-5 | SpringBootTest => Real\<SpringBootTest>
实现多个组件在Spring环境下的集成测试，验证框架的功能：拦截器、AOP、日志、事务处理