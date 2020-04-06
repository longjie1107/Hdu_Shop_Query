"""
@desc:
把从mysql导出的csv文件按照jieba外部词典的格式转为txt文件。
nz代表商铺名称。
"""
import pandas as pd

df = pd.read_csv('./shop.txt')
title = df['shop_title'].values

with open('./shop_title.txt', 'w',encoding="utf-8") as f:
    for t in title[1:]:
        if "(" in t:        #w去除没用的()
            t=t[:t.index("(")]
        f.write(t + ' ' + 'nz' + '\n')
        for i in range(0,len(t)-1):       #截取店名的两个字,使分词更精确
            newt=t[i:i+2]
            f.write(newt + ' ' + 'nz' + '\n')
        for i in range(0,len(t)-2):       #截取店名的三个字
            newt=t[i:i+3]
            f.write(newt + ' ' + 'nz' + '\n')
