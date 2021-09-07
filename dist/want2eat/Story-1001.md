### Table of Content
- [In Scope](#in-scope)
- [Out of Scope](#out-of-scope)
- [AC 1 获得订单收入，余额增加](#ac-1)
  - [示例 1-1 当前商户id：10001，账户余额0；订单id：aaaa-bbbb-cccc-dddd，获得收入100；更新后账户id：10001，账户余额为100，新增收入记录，关联订单id：aaaa-bbbb-cccc-dddd](#example-1-1)
  - [示例 1-2 当前商户id：10001，账户余额100；订单id：aaaa-bbbb-cccc-dddd，获得收入100；更新后账户id：10001，账户余额为200，新增收入记录，关联订单id：aaaa-bbbb-cccc-dddd](#example-1-2)
- [API Schema](#api-schema)
# Story-1001
### In Scope
作为 【入驻商家】，我想要 【完成订单后获得相应的余额收入】，以便于【查询余额和提现】
### Out of Scope
假设：此接口由下游服务 "订单管理应用服务" 在订单完成后自动调用，返回200时即为确认，返回500时下游服务会自动重试
### <span id='ac-1'>AC 1 </span>
获得订单收入，余额增加
#### <span id='example-1-1'>示例 1-1 当前商户id：10001，账户余额0；订单id：aaaa-bbbb-cccc-dddd，获得收入100；更新后账户id：10001，账户余额为100，新增收入记录，关联订单id：aaaa-bbbb-cccc-dddd</span>
##### 任务列表
 - **工序 1-1 | Mock<MerchantService.Service> | 15 mins**

	获取请求参数组装ViewObject，调用mock Service
	```
	API Call:
	> POST /merchant-account/balance/income
	< 200 OK
	```

----
 - **工序 1-3 | Mock<MerchantService.Repository> | 20 mins**

	组装Entity，调用mock Repository创建一条收入记录
	更新商户账户的余额为100，调用mock Repository进行保存

----
 - **工序 1-6 | Fake<MerchantService.DB> | 30 mins**

	测试Repository能够使用Entity操作fake 数据库并执行对应的SQL语句

----
##### 时序图
![ddca8aed-3f8d-44d2-aa7d-9a6113bf9cab](temp/story-1001/ddca8aed-3f8d-44d2-aa7d-9a6113bf9cab.svg)
#### <span id='example-1-2'>示例 1-2 当前商户id：10001，账户余额100；订单id：aaaa-bbbb-cccc-dddd，获得收入100；更新后账户id：10001，账户余额为200，新增收入记录，关联订单id：aaaa-bbbb-cccc-dddd</span>
##### 任务列表
 - **工序 1-3 | Mock<MerchantService.Repository> | 20 mins**

	组装Entity，调用mock Repository创建一条收入记录
	更新商户账户的余额为200，调用mock Repository进行保存

----
##### 时序图
![febd2947-61e6-411b-9ba4-dc20bff4422b](temp/story-1001/febd2947-61e6-411b-9ba4-dc20bff4422b.svg)
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

