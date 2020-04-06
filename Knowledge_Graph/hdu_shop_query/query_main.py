"""

@desc:main函数，整合整个处理流程。

"""
import jena_sparql_endpoint
import question2sparql

class QuerySystem:
    def __init__(self, question=None, *args, **kwargs):
        domain = kwargs.pop('domain', '')
        self.allowed_domains = filter(None, domain.split(','))
        super(QuerySystem, self).__init__(*args, **kwargs)
        self.question = question

    def run_main(self):
        # TODO 连接Fuseki服务器。
        fuseki = jena_sparql_endpoint.JenaFuseki()
        # TODO 初始化自然语言到SPARQL查询的模块，参数是外部词典列表。
        q2s = question2sparql.Question2Sparql(['./util/shop_title.txt'])

        my_query = q2s.get_sparql(self.question)
        if my_query is not None:
            result = fuseki.get_sparql_result(my_query)
            value = fuseki.get_sparql_result_value(result)

            # TODO 判断结果是否是布尔值，是布尔值则提问类型是"ASK"，回答“是”或者“不知道”。
            if isinstance(value, bool):
                if value is True:
                    return 'Yes'
                else:
                    return 'I don\'t know. :('
            else:
                # TODO 查询结果为空，根据OWA，回答“不知道”
                if len(value) == 0:
                    return 'I don\'t know. :('
                elif len(value) == 1:
                    return value[0]
                else:
                    output = ''
                    for v in value:
                        output += v + u'、'
                    return output[0:-1]

        else:
            # TODO 自然语言问题无法匹配到已有的正则模板上，回答“无法理解”
            return 'I can\'t understand. :('


if __name__ == '__main__':
    querySystem = QuerySystem("良品在哪?")
    res = querySystem.run_main()
    print(res)
