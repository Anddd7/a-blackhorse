### Table of Content
- [In Scope](#in-scope)
- [Out of Scope](#out-of-scope)
- [AC 1 获得订单收入，余额增加](#ac-1)
  - [示例 1-1 余额为0，订单收入100，计算后余额为100](#example-1-1)
  - [示例 1-2 余额为100，订单收入100，计算后余额为200](#example-1-2)
- [API Schema](#api-schema)
# Story-1001
### In Scope
作为 【入驻商家】，我想要 【完成订单后获得相应的余额收入】，以便于【查询余额和提现】
### Out of Scope
假设：此接口由下游服务 "订单管理应用服务" 在订单完成后自动调用，返回200时即为确认，返回500时下游服务会自动重试
### <span id='ac-1'>AC 1 </span>
获得订单收入，余额增加
#### <span id='example-1-1'>示例 1-1 余额为0，订单收入100，计算后余额为100</span>
##### 任务列表
 - **工序 1-1 | Mock<MerchantService.Service> | 15 mins**
 
	获取请求参数，调用mock Service
	```
	API Call:
	> POST /merchant-account/balance/income
	< 200 OK
	```
 
----
 - **工序 1-3 | Mock<MerchantService.Repository> | 20 mins**
 
	新增一条收入记录，mock Repository创建一条收入记录
	更新商户账户的余额 - 100
 
----
 - **工序 1-6 | Fake<MerchantService.DB> | 30 mins**
 
	测试Repository能够使用Entity操作数据库并执行对应的SQL语句
 
----
##### 时序图
![03efe4b7-080d-4d9c-b73b-53845b4f0151](temp/story-1001/03efe4b7-080d-4d9c-b73b-53845b4f0151.svg)
#### <span id='example-1-2'>示例 1-2 余额为100，订单收入100，计算后余额为200</span>
##### 任务列表
 - **工序 1-3 | Mock<MerchantService.Repository> | 20 mins**
 
	新增一条收入记录，mock Repository创建一条收入记录
	更新商户账户的余额 - 200
 
----
##### 时序图
![d6ab515d-ab45-4511-8e4d-504b8a7a0f91](temp/story-1001/d6ab515d-ab45-4511-8e4d-504b8a7a0f91.svg)
### API Schema
#### 订单收入API
> POST /merchant-account/balance/income
- 200 OK
  - Request
  ```json
  {
      "merchant_account_id": 100001,
      "order_id": "<uuid>",
      "amount": 100
  }
  ```
### 进程内架构设计
### MerchantService
餐品订购服务: 为商家提供接入平台的服务，包括开通账号、缴纳押金、提现入账余额、收据和发票开具的功能；平台可对违反合作协议的商家进行押金扣减、入账扣减
Tech Stack: **[Spring Boot, PostgreSQL]**
 
![49e51750-ea5a-41bc-a9b1-618b99a7547c](temp/story-1001/49e51750-ea5a-41bc-a9b1-618b99a7547c.svg)
#### 工序拆分
##### 工序 1-1 | Controller => Mock\<Service>
实现Controller获取Http请求参数，调用Service并获取ViewObject，再返回序列化的Json数据
##### 工序 1-2 | Service => Mock\<Client>
实现Service调用Client获取DTO，组装成ViewObject并返回
##### 工序 1-3 | Service => Mock\<Repository>
实现Service调用Repository获取Entity，组装成ViewObject并返回
##### 工序 1-4 | Client => Mock\<MQ>
实现Client调用MQ，通过DTO映射请求和返回的Json数据，验证发送和接收的数据正确
##### 工序 1-5 | Client => Mock\<Gateway>
实现Client调用Gateway，通过DTO映射请求和返回的Json数据
##### 工序 1-6 | Repository => Fake\<DB>
实现Repository调用DB，通过Entity映射数据库表，验证JPA的配置正确、数据库表创建正确、SQL语句书写正确
##### 工序 1-7 | SpringBootTest => Real\<SpringBootTest>
实现多个组件在Spring环境下的集成测试，验证框架的功能：拦截器、AOP、日志、事务处理