import pymysql

# 连接database
conn = pymysql.connect(
    host="127.0.0.1",
    user="root",password="root",
    database="hdu_demo_shop",
    charset="utf8")

conn2 = pymysql.connect(
    host="127.0.0.1",
    user="root",password="root",
    database="美食",
    charset="utf8"
)

cursor = conn.cursor()
cursor2 = conn2.cursor()

table_name = ["beauty","learning","restaurant","theater"]
sql = "select sp_name from shop"

cursor.execute(sql)
res=cursor.fetchall()
for rr in res:
    shop_name = rr[0]        # rr[0]为各个商铺的名字
    for table in table_name:
        sql2 = "select name from %s"%table
        cursor2.execute(sql2)
        res2=cursor2.fetchall()
        for rr2 in res2:
            if rr2[0]==shop_name:   #if equal, get 3 rate and assign to "hdu_demo_shop"
                # 从原始数据库获取该商铺的三项评分
                sql3 = "select quality_rate,environment_rate,service_rate from %s " \
                       "where name = '%s'"%(table,shop_name.replace("'","\\'"))  # for shop_name with ' , ' -> \'
                cursor2.execute(sql3)
                res3 = cursor2.fetchall()[0]
                quality_rate = res3[0]
                environment_rate = res3[1]
                service_rate = res3[2]
                avg = str(round((float(res3[0])+float(res3[1])+float(res3[2]))/3,1)) #保留一位
                #更新商铺的三项评分
                sql = "update shop set quality_rate=%s,environment_rate=%s,service_rate=%s,all_rate=%s " \
                      "where sp_name = '%s'"%(quality_rate,environment_rate,service_rate,avg,shop_name.replace("'","\\'"))
                cursor.execute(sql)



conn.commit()
cursor.close()
cursor2.close()
conn.close()
conn2.close()