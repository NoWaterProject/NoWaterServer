# coding:utf-8
from crontab import CronTab
import os
import sys

if __name__ == '__main__':
    my_user_cron = CronTab(user=True)

    current_path = os.path.abspath('.') + '/bin'

    command_admin_checkout_time = '/usr/bin/python ' + current_path + '/time_checkout.py ' + sys.argv[1] + ' > ' + current_path + '/time_checkout.log'

    my_user_cron.remove_all(command=command_admin_checkout_time)
    job = my_user_cron.new(command=command_admin_checkout_time)
    job.setall('*/1 * * * *')

    command_backup_db = "mysqldump -u root -pNoWater118 --extended-insert --all-databases > " + current_path + "/backupNoWater.sql"

    my_user_cron.remove_all(command=command_backup_db)
    job = my_user_cron.new(command=command_backup_db)
    job.setall('10 0 * * *')

    command_change_ad = '/usr/bin/python ' + current_path + '/change_ad.py > ' + current_path + '/change_ad.log'

    my_user_cron.remove_all(command=command_change_ad)
    job = my_user_cron.new(command=command_change_ad)
    job.setall('0 0 * * *')
    my_user_cron.write()