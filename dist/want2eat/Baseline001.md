### Table of Content
- [In Scope](#in-scope)
- [Out of Scope](#out-of-scope)
- [AC 1 当本月第一次提现，且提现金额小于当前余额时，提现成功](#ac-1)
  - [示例 1-1 当前余额100，提现100，当月无提现记录](#example-1-1)
- [AC 2 当提现金额大于当前余额时，提现失败](#ac-2)
  - [示例 2-1 当前余额99，提现金额100](#example-2-1)
- [AC 3 当本月已有提现记录时，提现失败](#ac-3)
  - [示例 3-1 当前余额100，提现100，但当月已有一笔提现记录](#example-3-1)
- [AC 4 当提现完成时，标记提现数据](#ac-4)
  - [示例 4-1 当前余额100，提现100，但当月已有一笔提现记录](#example-4-1)
- [API Schema](#api-schema)
# Baseline001
### In Scope
作为 【入驻商家】，我想要 【进行余额的提现】，以便于【将店铺运营的利润转化为实际的收入】

提现条件：
- 提现金额 < 当前余额
- 每月仅能提现一次
### Out of Scope
假设：所依赖的外部接口均已开发完成，直接调用即可
假设：提现完成后会由消息队列发起回调，提示提现完成
### <span id='ac-1'>AC 1 </span>
当本月第一次提现，且提现金额小于当前余额时，提现成功
#### <span id='example-1-1'>示例 1-1 当前余额100，提现100，当月无提现记录</span>
##### 任务列表
 - **工序 1-1 | Mock<MerchantService.Service> | 0 mins**
 
	获取请求参数`merchant_account_id, amount`，调用mock Service
	```
	API Call:
	> POST /merchant-account/balance/withdraw
	< 200 OK
	```
 
----
 - **工序 1-3 | Mock<MerchantService.Repository> | 0 mins**
 
	通过`merchant_account_id`查询当前余额，调用mock Repository返回商家账户信息
	通过`merchant_account_id, time.now()`查询当月提现记录，调用mock Repository返回提现记录
	新建一条提现记录
	mock Repository返回商家账户信息 - 余额100
	mock Repository返回提现记录 - 0条
	mock Repository创建提现记录
 
----
 - **工序 1-6 | Fake<MerchantService.DB> | 0 mins**
 
	创建JPA方法，调用fake DB预插入一条商户账户数据 - 余额100
	创建JPA方法，调用fake DB预插入一条提现记录数据 - 上月提现记录
 
----
 - **工序 1-2 | Mock<MerchantService.Client> | 0 mins**
 
	创建一条提现申请消息，调用mock Client进行发送
 
----
 - **工序 1-4 | Mock<MerchantService.MQ> | 0 mins**
 
	mock MQ收到了的消息请求中的余额 = 100
	```
	API Call:
	> POST /messages
	< 200 OK
	```
 
----
##### 时序图
![fd0fa0cf-494b-4512-a387-c49cf84ddfdc](temp/baseline001/fd0fa0cf-494b-4512-a387-c49cf84ddfdc.svg)
### <span id='ac-2'>AC 2 </span>
当提现金额大于当前余额时，提现失败
#### <span id='example-2-1'>示例 2-1 当前余额99，提现金额100</span>
##### 任务列表
 - **工序 1-1 | Mock<MerchantService.Service> | 0 mins**
 
	获取请求参数`merchant_account_id, amount`，调用mock Service
	mock Service抛出异常
	```
	API Call:
	> POST /merchant-account/balance/withdraw
	< 400 BAD_REQUEST
	```
 
----
 - **工序 1-3 | Mock<MerchantService.Repository> | 0 mins**
 
	通过`merchant_account_id`查询当前余额，调用mock Repository返回商家账户信息
	mock Repository返回商家账户信息 - 余额99
	抛出`余额不足`的业务异常
 
----
##### 时序图
![05b4a355-6a2b-43c8-899f-7571216f6d9d](temp/baseline001/05b4a355-6a2b-43c8-899f-7571216f6d9d.svg)
### <span id='ac-3'>AC 3 </span>
当本月已有提现记录时，提现失败
#### <span id='example-3-1'>示例 3-1 当前余额100，提现100，但当月已有一笔提现记录</span>
##### 任务列表
 - **工序 1-1 | Mock<MerchantService.Service> | 0 mins**
 
	获取请求参数`merchant_account_id, amount`，调用mock Service
	mock Service抛出异常
	```
	API Call:
	> POST /merchant-account/balance/withdraw
	< 400 BAD_REQUEST
	```
 
----
 - **工序 1-3 | Mock<MerchantService.Repository> | 0 mins**
 
	通过`merchant_account_id, time.now()`查询当月提现记录，调用mock Repository返回提现记录
	mock Repository返回提现记录 - 1条
	抛出`已提现`的业务异常
 
----
##### 时序图
![6243f0b0-a6da-452c-b669-b4f7cfdff6e4](temp/baseline001/6243f0b0-a6da-452c-b669-b4f7cfdff6e4.svg)
### <span id='ac-4'>AC 4 </span>
当提现完成时，标记提现数据
#### <span id='example-4-1'>示例 4-1 当前余额100，提现100，但当月已有一笔提现记录</span>
##### 任务列表
 - **工序 1-1 | Mock<MerchantService.Service> | 0 mins**
 
	获取请求参数`withdraw_id, updated_at`, 调用mock Service
	```
	API Call:
	> POST /merchant-account/balance/withdraw/{id}/confirmation
	< 200 OK
	```
 
----
 - **工序 1-3 | Mock<MerchantService.Repository> | 0 mins**
 
	通过`withdraw_id`查询已有提现记录，更新完成状态和完成时间
 
----
##### 时序图
![bac9290d-82e3-431d-8b71-b2a12156f8dd](temp/baseline001/bac9290d-82e3-431d-8b71-b2a12156f8dd.svg)
### API Schema
#### 提现API
> POST /merchant-account/balance/withdraw
- 200 OK
  - Request
  ```json
  {
      "merchant_account_id": 100001,
      "amount": 100.00,
      "currency": "CHN_YUAN",
      "channel": "WECHAT"
  }
  ```
- 400 BAD_REQUEST
  - Request
  ```json
  {
      "merchant_account_id": 100001,
      "amount": 100.00,
      "currency": "CHN_YUAN",
      "channel": "WECHAT"
  }
  ```
  - Response
  ```json
  {
      "message": "balance insufficient"
  }
  ```
#### 提现回调API
> POST /merchant-account/balance/withdraw/{id}/confirmation
- 200 OK
  - Request
  ```json
  {
      "merchant_account_id": 100001,
      "updated_at": "<timestamp_iso>"
  }
  ```
### 进程内架构设计
### MerchantService
餐品订购服务: 为商家提供接入平台的服务，包括开通账号、缴纳押金、提现入账余额、收据和发票开具的功能；平台可对违反合作协议的商家进行押金扣减、入账扣减
Tech Stack: **[Spring Boot, PostgreSQL]**
 
![713364b5-810f-4a2f-b6c6-1877e493e06d](temp/baseline001/713364b5-810f-4a2f-b6c6-1877e493e06d.svg)
#### 工序拆分
##### 工序 1-1 | Controller => Mock\<Service>
实现Controller获取Http请求参数，调用Service并获取ViewObject，再返回序列化的Json数据
##### 工序 1-2 | Service => Mock\<Client>
实现Service调用Client获取DTO，组装成ViewObject并返回
##### 工序 1-3 | Service => Mock\<Repository>
实现Service调用Repository获取Entity，组装成ViewObject并返回
##### 工序 1-4 | Client => Mock\<MQ>
实现Client调用MQ，通过DTO映射请求和返回的Json数据
##### 工序 1-5 | Client => Mock\<Gateway>
实现Client调用Gateway，通过DTO映射请求和返回的Json数据
##### 工序 1-6 | Repository => Fake\<DB>
实现Repository调用DB，通过JPA正确执行数据库访问并返回对应的Entity数据
##### 工序 1-7 | SpringBootTest => Real\<SpringBootTest>
实现所有组件进行集成测试