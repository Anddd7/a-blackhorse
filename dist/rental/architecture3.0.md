# Architecture Map of rental
##### ChangeLogs
refine the description of containers;
add required components;
![18efb239-67bb-4574-a62e-2085b6f99298](temp/18efb239-67bb-4574-a62e-2085b6f99298.svg)
## Frontend
### GeneralWebPortal
思沃租房通用版Web端: 供浏览用戶、个人用戶和经纪人用戶使用Web访问
Tech Stack: [React]
Owner: []
### IndividualApp4Android
思沃租房App个人版Android端: 供浏览用戶、个人用戶使用Android端APP访问, 以完成个人用戶相关的功能
Tech Stack: [Android, Kotlin]
Owner: []
### IndividualApp4IOS
思沃租房App个人版IOS端: 供浏览用戶、个人用戶使用IOS端APP访问, 以完成个人用戶相关的功能
Tech Stack: [Swift]
Owner: []
### OfficerApp4Android
思沃租房App经纪人版Android端: 供经纪人用戶使用Android端APP访问, 以完成经纪人相关的功能
Tech Stack: [Android, Kotlin]
Owner: []
### OfficerApp4IOS
思沃租房App经纪人版IOS端: 供经纪人用戶使用IOS端APP访问, 以完成经纪人相关的功能
Tech Stack: [Swift]
Owner: []
### ManagementPortal
后台管理系统Web端: 供思沃租房工作人员使用Web访问, 以完成后台管理功能
Tech Stack: [React]
Owner: []
## BFF
### WebApiBff
思沃租房Web Bff: 基于后端应用服务, 服务于思沃租房通用版Web端
Tech Stack: [go]
Owner: []
### MobileApiBff
思沃租房Mobile Bff: 基于后端应用服务, 服务于思沃租房通APP, 包括个人版和经纪人版, Android和IOS端
Tech Stack: [nodejs]
Owner: []
## ApplicationService
### ManagementApplication
后台管理应用服务: 向前端服务/应用提供后台管理的功能接口
Tech Stack: [Spring Boot]
Owner: []
### PopularizationApplication
推广服务应用服务: 向前端服务/应用提供推广服务和与充值服务相关的功能接口
Tech Stack: [Spring, Kotlin]
Owner: []
![17fc88a2-3a3d-4c66-9841-7c3e9fef2f7c](temp/17fc88a2-3a3d-4c66-9841-7c3e9fef2f7c.svg)
### RentalApplication
租赁信息应用服务: 向前端服务/应用提供租赁信息的展示、搜索、发布、更新、下架等功能接口
Tech Stack: [Spring Boot]
Owner: []
## CoreBizService
### PopularizationService
信息推广服务: 提供信息推广服务协议上下文中的业务能力接口
Tech Stack: [Spring Boot]
Owner: []
### PrepaidService
预充值服务: 提供预充值服务协议上下文中的业务能力接口
Tech Stack: [Spring Boot]
Owner: []
![bf2fa5e5-d8f6-4d34-b62e-e58057c97540](temp/bf2fa5e5-d8f6-4d34-b62e-e58057c97540.svg)
## DomainService
### AuthenticationService
鉴权认证服务: 负责提供用戶的身份和鉴权等功能
Tech Stack: [Spring Boot]
Owner: []
![34e6ff01-f55d-4750-a3b9-c3fafdcb775d](temp/34e6ff01-f55d-4750-a3b9-c3fafdcb775d.svg)
### RentalInfoMgmtService
房屋信息管理系统: 存储、查询和管理房屋租赁信息的 CMS 系统
Tech Stack: [Strapi]
Owner: []
### UserManagementService
用戶账戶管理系统: 负责管理用戶账戶,包括常⻅的用戶账戶相关功能
Tech Stack: [Spring Boot]
Owner: []