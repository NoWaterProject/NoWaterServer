# coding:utf-8
from crontab import CronTab
import os
import sys

if __name__ == '__main__':
    current_path = os.path.abspath('.') + '/bin'
    my_user_cron = CronTab(user=True)
    if sys.argv[1] == "1":
        command_change_ad = '/usr/bin/python ' + current_path + '/change_ad.py > ' + current_path + '/change_ad.log'
        my_user_cron.remove_all(command=command_change_ad)
        my_user_cron.write()
        job = my_user_cron.new(command=command_change_ad)
        print sys.argv[2]
        print sys.argv[3]
        aim_time = sys.argv[2] + ' ' + sys.argv[3] + ' * * *'
        job.setall(aim_time)
        my_user_cron.write()
    else:
        command_db_backup = 'mysqldump -u root -pNoWater118 --extended-insert --all-databases > ' + current_path + '/backupNoWater.sql'
        my_user_cron.remove_all(command=command_db_backup)
        my_user_cron.write()
        job = my_user_cron.new(command=command_db_backup)
        aim_time = sys.argv[2] + ' ' + sys.argv[3] + ' * * *'
        job.setall(aim_time)
        my_user_cron.write()