# coding:utf-8
import requests
import sys

if __name__ == '__main__':
    response = requests.post("http://localhost:16120/admin/checkout/time", data={'key': sys.argv[1]})
    print response.content
