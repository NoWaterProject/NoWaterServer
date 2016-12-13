# NoWater接口文档
* 目前日志系统仍未完成
* 连接数据库的方案未确定，先用jdbc吧
* 除登录、注册、和验证码相关的接口外，首先从cookie中获取token，然后在redis中获取用户名，如果获取不到，则未登录。

---
## Customer相关接口（所有开头都用customer）
* 目前验证码还未找到解决方案
* 还剩商品详情页、购物车、收藏、购买页面、订单页面

* **customer/register**
    需要判断Name是否唯一（需要把所有Name从数据库中取出，然后判断，避免被SQL注射），地址是否合法（从redis中获取数据并判断），电话号码是否合法，注册成功需要将用户数据添加到数据库（添加用户名时判断一下是否有双引号）。

    所需参数：
    
    * name（string，用户名，必须在数据库中唯一）
    * password（string，密码，MD5加密后的32位字符串）
    * telephone（string，电话号码，电话号码必须合法）
    * email（string，邮箱地址，需要进行邮箱验证）
    * address1（string，香港岛，九龙，or新界）
    * address2（string，香港的哪个区域）
    * address3（string，用户填的地址）
    * postCode（string，邮编）
    * firstName（string，首名字）
    * lastName（string）

    返回：
    
    * 状态码（status）：
        * 200（注册成功）
        * 300（用户名不合法，已被注册）
        * 400（电话号码不合法）
        * 500（地址不合法）

* **customer/login**
    先判断用户名是否存在，之后再判断密码是否正确。生成token，以token为键值向redis中写入user_id，同时以user_id为键值写入token，将token以**token**为关键字写入cookie。

    所需参数：
    
    * name（string，用户名）
    * password（string，密码）
    
    返回：
    
    * 状态码（status）：
        * 200（登录成功）
        * 300（用户名不存在或密码错误）

### 首页

* **customer/isLogin**
    用于判断是否登录，直接从cookie中获取信息进行比对。如果已登录，返回用户名和购物车数量。

    所需参数：无
    
    返回：
    
    * 状态码（status）：
        * 200（已登录）
        * 300（未登录，此时只返回状态码）
    * userInformation（数组）：
        * name（string，用户名）
        * cartNum（int，购物车数量）

* **customer/shop/ad**
    用于返回店铺的Id及广告图片在对象存储器中的文件名。店铺Id和广告图片的文件名从redis里拿，key为ShopAd。不用查cookie是否登录。

    所需参数：无
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 400（服务器错误）
    * data（数组）：
        * shopId（int，店铺Id）
        * adPhotoUrl（string，存储照片的URL）
        
* **customer/product/ad**
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

* **customer/cart/adding**
    用于商品加入购物车，只需要存储sizeId，userId，具体看数据库中的cart表。

    所需参数：

    * sizeId（int，尺寸Id）
    * num（int，数量，默认为1，非必须）

    返回：
    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
        * 400（已加入购物车）
        
### 搜索界面

* **customer/search**
    用于返回搜索结果，网站搜索结果和店内搜索结果都用这个接口。使用**Like**关键字。搜索结果存redis，便于加载更多，searchKey用user_id+路径，使用md5生成。

    所需参数：

    * keyWord（string，单词串，后台要按**空格**进行分割成一个个单词）
    * count（int，需要加载多少个）
    * shopId（int，店铺Id，非必须，用于店铺内搜索）
    * searchKey（string，redis缓存的关键字，非必须，用于下拉加载）
    * startId（int，返回结果首个关键字，非必须）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
        * 400（已加载完全部，也就是最后count>actualCount）
    * searchKey（string，获取redis缓存的关键字）
    * nextStartId（int，下拉加载后的第一个Id）
    * actualCount（int，实际数量）
    * data（数组）：
        * productId（int，商品id）
        * productName（string，商品名称）
        * price（double，单价）
        * photoIdUrl（string，存储照片的URL）
        * quantityStock（int，库存数量）

### 店铺界面

* **customer/shop/class/list**
    获取店铺的商品类别。
    
    所需参数：
    
    * shopId（int，店铺Id）
    
    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录，一样成功）
        * 400（商铺不存在，不返回后续数据）
    * data（数组）：
        * classId（int，类别Id）
        * className（string，类别名）

* **customer/shop/class/product**
    获取某个店铺的某类商品。

    所需参数：

    * shopId（int，店铺Id）
    * classId（int，类别Id）
    * count（int，需要加载多少个）
    * searchKey（string，redis缓存的关键字，非必须，用于下拉加载）
    * startId（int，返回结果首个关键字，非必须）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录，一样成功）
        * 400（已全部加载完）
        * 500（店铺不存在这个类别的商品）
        * 600（商铺不存在）
    * actualCount（int，实际数量）
    * searchKey（string，获取redis缓存的关键字）
    * nextStartId（int，下拉加载后的第一个Id）
    * data（数组）：
        * productId（int，商品id）
        * productName（string，商品名称）
        * price（double，单价）
        * photoIdUrl（string，存储照片的URL）
        * quantityStock（int，库存数量）

* **customer/shop/product/ad**
    获取某个商店的广告。在Shop Owner允许用户上传广告照片，然后绑定商品Id。

    所需参数：

    * shopId（int，店铺Id）

    返回：

    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录）
    * adNum（int，广告数量，即数组大小，也就是界面放多少个广告）
    * data（数组）：
        * productId（int，商品Id）
        * photoUrl（string，广告照片的Url）

---
## Shop Owner相关接口

* **shop-owner/status**
    用于判断shop owner的状态，处于未申请，处于申请中，已成为卖家。

    所需参数：无

    返回：

    * 状态码（status）：
        * 200（已成为卖家）
        * 300（用户未登录）
        * 400（用户未申请成为卖家）
        * 500（用户的申请正在处于审批状态）

* **shop-owner/apply**
    用于用户申请成为shop owner。同样需要cookie验证username是否正确。

    所需参数：

    * email（string，邮箱）
    * shopName（string，店名）
    * telephone（string，电话号码，需要以6和9开头，并且为8位）
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（用户未登录，失败）
        * 400（邮箱不规范）
        * 500（店名已有人使用）
        
* **shop-owner/products/list**
    用于展示添加的商品列表。注意data按productId倒序返回。（可以考虑是否按修改时间倒序）

    所需参数：
    
    * startId（int，起始商品Id，非必须）
    * count（int，需要加载多少个）

    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（身份错误，非商家，或者没有登录）
    * endId（int，下一页的起始Id）
    * actualCount（int，实际数量）
    * data：
        * productId（int，商品编号）
        * productName（string，商品名称）
        * photoUrl（string，照片的Url，数据库中的ad_photo_url）
        
* **shop-owner/products/detail**
    用于返回商品详情

    所需参数：
    
    * productId
    
    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（该商品不存在）
    * data：
        * productId（商品id）
        * productName（商品名称）
        * price（单价）
        * photoIdUrl（存储照片的URL）
        * quantityStock（库存数量）
        
* **shop-owner/products/edit**
    用于添加或编辑商品接口。当没有传productId时，视为添加商品；否则为修改商品。

    所需参数：
    
    * productId（int，商品Id，非必须）
    * productName（string，商品名称）
    * price（int，单价）
    * photoIdUrl（string，存储照片的URL）
    * quantityStock（int，库存数量）

    返回：
    
    * 状态码（status）：
        * 200（成功）
        * 300（身份错误，cookie里存的token不对）
        * 400（数据类型错误）
---
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

* **admin/shop/applyList**
    用于展示处于申请状态的shop的记录。

* **admin/shop/handle**
    用于处理申请状态的shop。

* **admin/product/ad/manage**
    管理首页的商品广告。需要将结果写进redis。

    所需参数：

    * sizeId（int，尺寸Id）
    
    