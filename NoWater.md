# (Group 6: Our Name is NoWater) ParknShop interface documentation
* How to confirm the user? We get the token from cookie, and use the token searching redis to get the userId, and use the userId searching redis to get the real token. If the token from cookie is equal to the real token, we can confirm the user is login.
* The Return should be JSON.

## Customer Interface（interface path is started with customer）

* **customer/register** （完成）

    The interface is used to customer's register.
    First, we need to judge whether the name posted is unique. We should get all the name from MySQL, then judge whether the name in them.
    Second, we need to judge whether the address is legal. We get the address array from redis.
    Third, we need to judge whether the telephone is started with 6 and 9, and the length of it is 8.

    Request Param：
    
    * name (string, the username used in login)
    * password (string, it should be encrypted in MD5)
    * telephone (string)
    * address1 (string, Kowloon(KLN), NT_Island(NT), or HongkongIsland(HK))
    * address2 (string, District)
    * address3 (string, Address)
    * postCode (string)
    * firstName (string)
    * lastName (string)

    Return：
    
    * status：
        * 200 (success)
        * 300 (username has been register)
        * 400 (telephone error)
        * 500 (address error)

* **customer/login**    （添加黑名单功能未完成）
    
    先判断用户名是否存在，之后再判断密码是否正确。生成token，以token为键值向redis中写入user_id，同时以user_id为键值写入token，将token以**token**为关键字写入cookie。

    Request Param：
    
    * name (string, username)
    * password (string, it should be encrypted in MD5)
    
    Return：
    
    * status：
        * 200 (登录成功)
        * 300 (用户名不存在或密码错误)
        * 400（用户已进入黑名单，不准登录）

### 首页

* **添加到购物车接口**、**加入收藏接口** 请看**购物车/收藏相关**
* **搜索接口**、**按类检索接口** 请看**搜索/按类检索相关**
* **店铺信息**请看本模块

* **customer/isLogin**  （完成）

    用于判断是否登录，直接从cookie中获取信息进行比对。如果已登录，返回用户名和购物车数量。

    所需参数：无
    
    返回：
    
    * 状态码（status）：
        * 200（已登录）
        * 300（未登录，此时只返回状态码）
    * userInformation（数组）：
        * name（string，用户名）
        * cartNum（int，购物车数量）

* **customer/shop/ad**  （完成）

    用于返回店铺的Id及广告图片在对象存储器中的文件名。店铺Id和广告图片的文件名从redis里拿，key为ShopAd。不用查cookie是否登录。

    所需参数：无
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 400（服务器错误）
    * data（数组）：
        * shopId（int，店铺Id）
        * adPhotoUrl（string，存储照片的URL）
        
* **customer/product/ad**   （完成）

    用于返回商品的Id及商品信息，商品Id从redis里拿，然后再把商品的查询封装起来一下。不用查询cookie是否登录。

    所需参数：无
    
    返回：

    * 状态码（status）：
        * 200（成功）
        * 400（服务器错误）
    * data（数组）：
        * sizeId（int，尺寸Id，用于加入购物车）
        * productId（int，商品id）
        * productName（string，商品名称）
        * price（double，单价）
        * photoIdUrl（string，存储照片的URL，用product表中的product_photo_url）

### 购物车/收藏相关

* 购物车结算接口，请看**订单相关**

* **customer/cart/adding**      （未完成）

    用于商品加入购物车，只需要存储商品Id，userId，具体看数据库中的cart表。
    
    如果这个商品已加入购物车，那么该商品在购物车中的数量+1，购物车商品种类数量不变（cartNum不变）。

    num不允许为非正数。addType为0时用于加入购物车按钮，为1时用于购物车列表编辑。

    所需参数：

    * productId（int，商品Id）
    * addType（int，0代表**数据库中的数量**直接加上**num**，1代表**num为最终数量**）
    * num（int，数量，非必须，默认为1）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
        * 500（**前端不用考虑**，该id不合法，小于0或大于目前的最大值）
        * 600（num错误）
        
* **customer/cart/deleting**        （未开始）

    用于删除购物车中的商品。只需要cartId即可。需要对比cartId和user是否能对应上。

    所需参数：

    * cartId（int，购物车编号）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
        * 500（**前端不用考虑**，非该用户的购物车，不准删除）

* **customer/cart/list**            （未开始）

    用于展示购物车列表。startId是下一个开头的cartId，按cartId降序给出。

    所需参数：

    * startId（int，开头项Id，非必须，默认为0）
    * count（int，单页数量）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
    * startId（int，下一页开头Id）
    * data（数组）：
        * cartId（int，购物车编号）
        * userId（int，用户Id）
        * num（int，数量）
        * shop
            * shopId（int，商铺Id）
            * shopName（string，店名）
            * ownerId（int，店主Id）
            * email（string，电子邮箱）
            * telephone（string，电话）
            * status（int，状态码）
        * product
            * productId（int，商品Id）
            * classId（int，类别Id）
            * productName（string，商品名）
            * price（int，价格）
            * quantityStock（int，库存数量）
            * isDel（int，是否已下架）
            * photo（数组，都是url，展示时展示第一张图片）

* **customer/favorite/adding**      （未完成）
    
    用于添加到收藏。需要favoriteType：**0**为商品，**1**为店铺；id：对应的商品或店铺id。

    所需参数：

    * favoriteType（int，0或1）
    * id（int，店铺或商品id）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
        * 400（**前端不用考虑**，favoriteType error）
        * 500（**前端不用考虑**，该商品或店铺id不合法，小于0或大于目前的最大值）

* **customer/favorite/deleting**        （未开始）

    用于删除收藏中的店铺或商品。同样需要比对favoriteId和userId。

    所需参数：

    * favoriteId（int，收藏Id）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
        * 500（**前端不用考虑**，非该用户的favoriteId）

* **customer/favorite/list**            （未开始）

    用于展示收藏列表。startId是下一个开头的favoriteId，按favoriteId降序给出。

    所需参数：

    * favoriteType（int，0或1）
    * startId（int，非必须，默认为0）
    * count（int，单页数量）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
        * 400（**前端不用考虑**，favoriteType error）
        * 500（**前端不用考虑**，startId error）
    * startId（int，下次发送的startId）
    * data（favoriteType 为0）：
        * productId（int，商品Id）
        * shopId（int，店家Id）
        * classId（int，类别Id）
        * productName（string，商品名）
        * price（float，商品价格）
        * quantityStock（int，库存数量）
        * isDel（int，是否已下架）
        * photo（数组，都是url，展示时展示第一张图片）
    * data（favoriteType 为1）：
        * shopId（int，商铺Id）
        * shopName（string，店名）
        * ownerId（int，店主Id）
        * email（string，电子邮箱）
        * telephone（string，电话）
        * status（int，状态码）

### 搜索/类型检索相关

* **customer/product/search**           （已完成）

    用于返回搜索结果，网站搜索结果和店内搜索结果都用这个接口。使用**Like**关键字。

    所需参数：

    * keyWord（string，关键词）
    * count（int，需要加载多少个）
    * shopId（int，店铺Id，非必须，默认为0）
    * startId（int，开始的Id，非必须，默认为0）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 400（多余请求）
    * startId（int，下次使用的startId）
    * data（数组）：
        * productId（int，商品Id）
        * shopId（int，店家Id）
        * classId（int，类别Id）
        * productName（string，商品名）
        * price（float，商品价格）
        * quantityStock（int，库存数量）
        * isDel（int，是否已下架）
        * photo（数组，都是url，展示时展示第一张图片）

* **customer/class/product**                （已完成）
    获取某个店铺的某类商品。

    所需参数：

    * shopId（int，店铺Id，非必须，默认为0，表示所有商店）
    * classId（int，类别Id，非必须，默认为0，表示所有类别的商品）
    * count（int，需要加载多少个）
    * startId（int，返回结果首个关键字，非必须，默认为0，首次请求）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录，一样成功）
        * 500（商铺不存在）
        * 600（classId错误，不在0到9之间）
    * actualCount（int，实际数量）
    * startId（int，下拉加载后的第一个Id）
    * data（数组）：
        * productId（int，商品Id）
        * shopId（int，店家Id）
        * classId（int，类别Id）
        * productName（string，商品名）
        * price（float，商品价格）
        * quantityStock（int，库存数量）
        * isDel（int，是否已下架）
        * photo（数组，都是url，展示时展示第一张图片）

### 订单/结算相关

* 订单状态说明：
    * -3：预订单状态，订单未确认
    * -2：已退款取消订单（专为广告设计）
    * -1：订单已取消（用户未付款手动取消），或者超时未付款（超时时间为1小时）
    *  0：**用于all order**
    *  1：订单已确认，待付款
    *  2：已付款，待发货
    *  3：店家已发货，待收货
    *  4：待评价（一天后跳到订单完成）
    *  5：订单完成；

* **order/prepare**         （进入测试阶段）

    用于生成待确认的订单。比如用于点了**立即购买**，或者在购物车中点了**结算**，或者申请广告界面点了申请。此时状态为-3。

    所需参数：

    * orderType（int，订单类型：0为customer立即购买订单，1为shop owner申请ParknShop首页商品广告订单，2为shop owner申请ParknShop首页店铺广告订单，3为customer购物车结算订单）
    * productId（int，商品Id，当orderType为0时，就是customer下单的商品。当orderType为1时，需要做广告的商品。当orderType为2时，productId为0）
    * price（float，单价，非必须，如果orderType为0，就不用传了）
    * num（int，数量）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（**前端不用考虑**，用户未登录）
    * orderIdList（JSON数组）
    
* **order/confirm**     （进入测试状态）

    订单确认，跳到付款界面。状态由-3到1。

    所需参数：

    * orderIdList（JSON数组，订单Id）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（**前端不用考虑**，用户未登录）
        * 400（**前端不用考虑**，orderId不存在）
        * 500（**前端不用考虑**，orderId和用户对不上）

* **order/price**       （进入测试状态）

    订单付款接口，用于付款界面。状态由1到2。

    所需参数：

    * orderIdList（JSON string，orderId列表）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（**前端不用考虑**，用户未登录）
        * 400（**前端不用考虑**，用户和orderId对应不上）
        
* **order/taken/delivery**
    
    确认收货。状态由3到4。
    
    所需参数：
    
    * orderId（int，订单Id）
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
        * 400（订单错误）
        
* **order/list**

    用于展示各个状态的order。
    
    所需参数：
    
    * status（订单状态）
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
    * data（数组）：
        * orderId（int，订单Id）
        * orderType（int，订单类型）
        * productId（int，商品Id）
        * time（string，生成预订单时间）
        * initiatorId（int，发起者Id）
        * targetId（int，目标店铺Id）
        * address（string，地址）
        * express（String，快递名称）
        * expressCode（String，邮单号）
        * num（int，数量）
        * price（float，单价）
        * sumPrice（float，总价）
        * status（int，状态）
        
* **order/cancel**
    
    用于取消订单。状态由1到-1。
    
    所需参数：
    
    * orderId（int，订单Id）
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
        * 400（订单错误）

### 店铺/商品展示相关

* **customer/shop/info**        （进入测试阶段）

    用于返回店铺信息。（用于店铺首页，也可以用于商品详情页）

    所需参数：

    * shopId（int，店铺Id）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 400（**前端不用考虑**，shopId不存在）
    * shopId（int，商铺Id）
    * shopName（string，店名）
    * ownerId（int，店主Id）
    * email（string，电子邮箱）
    * telephone（string，电话）
    * status（int，状态码）
    * classList（数组）：
        * classId（int，类别Id）
        * className（string，类别名）

* **customer/product/show**         （进入测试阶段）
    
    用于商品详情页。

    所需参数：

    * productId（int，商品Id）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 400（**前端不用考虑**，商品不存在）
        * 500（商品已下架，此时仍然会返回商品信息）
    * data
        * shop
            * shopId（int，商铺Id）
            * shopName（string，店名）
            * ownerId（int，店主Id）
            * email（string，电子邮箱）
            * telephone（string，电话）
            * status（int，状态码）
        * product
            * productId（int，商品Id）
            * classId（int，类别Id）
            * productName（string，商品名）
            * price（int，价格）
            * quantityStock（int，库存数量）
            * isDel（int，是否已下架）
            * photo（数组，都是url，展示所有图片）

## Shop Owner相关接口

* **shop-owner/status**     （已完成）

    用于判断shop owner的状态，处于未申请，处于申请中，已成为卖家。

    所需参数：无

    返回：

    * 状态码（status）：
        * 200（已成为卖家）
        * 300（用户未登录）
        * 400（用户未申请成为卖家）
        * 500（用户的申请正在处于审批状态）
        * 600（已拒绝）
        * 700（正在进行邮箱验证）
    * data：
        * shopId（int，商铺Id）
        * shopName（string，店名）
        * ownerId（int，店主Id）
        * email（string，电子邮箱）
        * telephone（string，电话）
        * status（int，状态码）
    * photo（数组，都是url）

* **shop-owner/apply**  （有修改，增加支付宝账号，未完成）

    用于用户申请成为shop owner。同样需要cookie验证username是否正确。

    所需参数：

    * email（string，邮箱）
    * shopName（string，店名）
    * telephone（string，电话号码，需要以6和9开头，并且为8位）
    * aliPay（string，支付宝账号）
    * fileNameList（JSON数组字符串，已上传的照片名）
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录，失败）
        * 400（邮箱不规范）
        * 500（店名已有人使用）
        * 600（正在审查）
        * 610（邮箱未验证）
        * 700（已成为卖家）
        * 800（email格式错误，没有@）
        * 900（电话号码格式错误）
        * 1000、1010、1020、1030、1040（服务器错误）
        
* **shop-owner/products/list**          （已完成）

    用于返回该店铺所有的商品列表

    所需参数：

    * startId（int，开始的Id，非必须，如果是第一页，则不需要此参数）
    * count（int，条目数量上限）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（用户名不正确）
        * 400（startId为-1，全部加载完成，多余请求，不予以返回数据）
    * actualCount（int，实际返回数量）
    * endId（int，下一页开始的商品Id，如果全部加载完，则返回-1）
    * data（数组）：
        * productId（int，商品Id）
        * shopId（int，店家Id）
        * classId（int，类别Id）
        * productName（string，商品名）
        * price（float，商品价格）
        * quantityStock（int，库存数量）
        * isDel（int，是否已下架）
        * photo（数组，都是url）
        
* **shop-owner/products/edit**          （已完成）

    用于添加和修改商品

    所需参数：

    * productId（int，在修改商品时发送）
    * classId（int，商品所属类别）
    * productName（string，商品名）
    * price（int，价格）
    * quantityStock（int，库存数量）
    * detailPhotoList（上传的商品详情的照片，注：parknshop首页商品广告的图片、搜索结果页的图片、默认使用第一张）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
        * 400（该用户不是商家）
        * 500（商品名重复）
        * 1010、1020、1030、1040（服务器错误）

### Shop Owner Order

* **shop-owner/order/delivery**

    用于确认发货。状态由2到3。
    
    所需参数：
    
    * express（string，快递公司）
    * expressCode（string，邮单号）
    * orderId（int，订单Id）
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
        * 400（订单不存在）

* **photo_type**
    * 0 店铺广告照片
    * 2 商品详情照片
    * 3 店主身份证照片

* **picture/upload**                    （已完成）

    上传照片的接口

    所需参数：

    * goodsPic[]

    返回：

    * 状态码（status）
        * 200（成功）
        * 300（用户未登录，暂时没有cookie验证）
        * 400（失败）

## Admin相关接口
* 查看管理员**cookie**时，身份验证是：**admin_token**的值。
    
* **admin/login**
    用于管理员登录。首先从数据库中把所有账号密码拿出，放进一个list。然后看这个用户名是否存在，不存在返回300。然后再看密码是否正确，不正确也返回300，正确的话，cookie中写入uuid生成的值，状态码返回200。

    所需参数：

    * name（string，用户名）
    * password（string，密码）
    
    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（密码错误或用户名错误）

* **admin/shop/applyList**          （已完成，后续补全文档）
    用于展示处于申请状态的shop的记录。

* **admin/shop/handle**             （已完成，后续补全文档）
    用于处理申请状态的shop。

* **admin/shop/list**

    用于展示商家列表

    所需参数：
    
    * shopType（int，如果为1，则为正常店家，如果为-1，则为黑名单商家）
    * count（int，数量）
    * startId（int，开始的Id，非必须，默认为0）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（admin未登录）
    * data：
        * shopId（int，商铺Id）
        * shopName（string，店名）
        * ownerId（int，店主Id）
        * email（string，电子邮箱）
        * telephone（string，电话）
        * status（int，状态码）
    
* **admin/customer/list**

    用于展示customer列表

    所需参数：
    
    * customerType（int，用户类型，1为正常用户，-1为黑名单用户）
    * count（int，数量）
    * startId（int，开始的Id，非必须，默认为0）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（admin未登录）
    * data：
        * userId（int，用户Id）
        * name（string，用户名）
        * telephone（string，电话号码）
        * address1（string）
        * address2（string）
        * address3（string）
        * postCode（string）
        * firstName（string）
        * lastName（string）
        * status（int）

* **admin/customer/blacklist/adding**
    
    加入黑名单的用户，不能登录，马上强制退出。如果开了店铺，则关闭店铺（处于已回绝状态）。

    所需参数：
    
    * userId（int，需要加入黑名单的用户的Id）
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（管理员未登录）
        * 400（该用户已删除）

* **admin/customer/delete**

    删除用户
    
    所需参数：
    
    * userId（int，需要从黑名单中移除的店铺Id）
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（管理员未登录）
        * 400（userId不存在）

* **admin/customer/blacklist/deleting**

    恢复加入了黑名单的用户
    
    所需参数：
    
    * userId（int，需要删除的用户Id）
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（管理员未登录）
        * 400（userId不存在）

* **admin/shop/blacklist/adding**

    加入黑名单的商家，关闭店铺（处于已回绝状态）。不允许下单

    所需参数：
    
    * shopId（int，需要加入黑名单的店铺的Id）
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（管理员未登录）
        * 400（shopId不存在）
        * 500（shopId已删除）

* **admin/shop/delete**

    删除商铺
    
    所需参数：
    
    * shopId（int，需要删除的shopId）
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（管理员未登录）
        * 400（shopId不存在）

* **admin/shop/blacklist/deleting**

    恢复加入了黑名单的店铺
    
    所需参数：
    
    * shopId（int，需要从黑名单中移除的店铺Id）
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（管理员未登录）
        * 400（shopId不存在）
        * 500（shopId已删除）
