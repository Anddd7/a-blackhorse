[TOC]
# Architecture Map of want2eat
##### ChangeLogs
绘制需要的架构信息
![9a98f48f-bda4-4369-9843-c81dd7eb2c44](temp/9a98f48f-bda4-4369-9843-c81dd7eb2c44.svg)
## CoreBizService
### OrderService
餐品订购服务: 以订单为核心，为订餐用户提供下单、支付、退款、投诉等功能；商户可以接单、拒单，完成备餐后即可获得订单收益
Tech Stack: **[Spring Boot, PostgreSQL]**
 
![f171cfaa-e36e-448e-9827-5f7ec6529c21](temp/f171cfaa-e36e-448e-9827-5f7ec6529c21.svg)