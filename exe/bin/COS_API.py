# coding:utf-8
from qcloud_cos import DelFileRequest
from qcloud_cos import UploadFileRequest
from qcloud_cos import CosClient
import sys

if __name__ == '__main__':
    if len(sys.argv) < 3:
        print "argv error"

    bucket = u'koprvhdix117'
    app_id = 10038234
    secret_id = u'AKIDkTQGMuCeJvtTTlqg911BfF393ghumqHp'
    secret_key = u'ZE1uBa6jfbsB0vVyfbWhw5JuZKPwaEwh'

    cos_client = CosClient(app_id, secret_id, secret_key)

    if sys.argv[1] == "upload":
        cos_path = unicode(sys.argv[2])
        local_path = unicode(sys.argv[3])

        request = UploadFileRequest(bucket, cos_path, local_path)
        upload_file_ret = cos_client.upload_file(request)
        print upload_file_ret
    elif sys.argv[1] == "delete":
        cos_path = unicode(sys.argv[2])

        request = DelFileRequest(bucket, cos_path)
        delete_file_ret = cos_client.del_file(request)
        print delete_file_ret
    else:
        print "argv error"
