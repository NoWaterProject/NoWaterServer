# coding:utf-8

import redis

if __name__ == '__main__':
    r = redis.Redis(host='localhost', port=6379, db=0)
    prepareShopAd = r.get('prepareShopAd')
    r.set('ShopAd', prepareShopAd)
    print 'set ShopAd'
    print prepareShopAd
    r.delete('prepareShopAd')
    print 'delete prepareShopAd'

    prepareProductAd = r.get('prepareProductAd')
    r.set('ProductAd', prepareProductAd)
    print 'set ProductAd'
    print prepareProductAd
    r.delete('prepareProductAd')
    print 'delete prepareProductAd'