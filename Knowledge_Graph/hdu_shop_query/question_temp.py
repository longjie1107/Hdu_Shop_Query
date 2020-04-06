"""
@desc:
设置问题模板，为每个模板设置对应的SPARQL语句。demo提供如下模板：

1. 某商店在哪里
2. 某商店在几楼
3. 某类型的商店有哪些
4. 某楼层有哪些商店
5. 某商店的消费水平是多少
6. 某商店的某项评分是多少
"""
from refo import finditer, Predicate, Star, Any, Disjunction
import re

# TODO SPARQL前缀和模板
SPARQL_PREXIX = u"""
PREFIX : <http://www.hdudemo.com#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
"""

SPARQL_SELECT_TEM = u"{prefix}\n" + \
                    u"SELECT DISTINCT {select} WHERE {{\n" + \
                    u"{expression}\n" + \
                    u"}}\n"

SPARQL_COUNT_TEM = u"{prefix}\n" + \
                   u"SELECT COUNT({select}) WHERE {{\n" + \
                   u"{expression}\n" + \
                   u"}}\n"

SPARQL_ASK_TEM = u"{prefix}\n" + \
                 u"ASK {{\n" + \
                 u"{expression}\n" + \
                 u"}}\n"


class W(Predicate):
    def __init__(self, token=".*", pos=".*"):
        self.token = re.compile(token + "$")
        self.pos = re.compile(pos + "$")
        super(W, self).__init__(self.match)

    def match(self, word):
        m1 = self.token.match(word.token)
        m2 = self.pos.match(word.pos)
        return m1 and m2


class Rule(object):
    def __init__(self, condition_num, condition=None, action=None):
        assert condition and action
        self.condition = condition
        self.action = action
        self.condition_num = condition_num

    def apply(self, sentence):
        matches = []
        for m in finditer(self.condition, sentence):
            i, j = m.span()
            matches.extend(sentence[i:j])

        return self.action(matches), self.condition_num


class KeywordRule(object):
    def __init__(self, condition=None, action=None):
        assert condition and action
        self.condition = condition
        self.action = action

    def apply(self, sentence):
        matches = []
        for m in finditer(self.condition, sentence):
            i, j = m.span()
            matches.extend(sentence[i:j])
        if len(matches) == 0:
            return None
        else:
            return self.action()


class QuestionSet:
    def __init__(self):
        pass

    @staticmethod
    def shop_position_question(word_objects):
        """
        某商店在哪里
        :param word_objects:
        :return:
        """
        select = u"?name ?x"

        sparql = None
        for w in word_objects:
            if w.pos == pos_shop:
                # filter+regex表示模糊查询,输入的商铺名字可以不全,参数'i'表示不区分大小写
                e = u"?s :shopName ?name." \
                    u"?s :shopAddress ?x." \
                    u"filter regex(?name,'{shop}','i')".format(shop=w.token)

                sparql = SPARQL_SELECT_TEM.format(prefix=SPARQL_PREXIX,
                                                  select=select,
                                                  expression=e)
                break
        return sparql

    @staticmethod
    def shop_rating_question(word_objects):
        """
        某商店的某项评分是多少
        :param word_objects:
        :return:
        """
        select = u"?name ?x"
        sparql = None
        shop = None
        keyword = None
        for r in genre_keyword_rules:
            keyword = r.apply(word_objects)
            if keyword is not None:
                break

        for w in word_objects:
            if w.pos == pos_shop:
                shop = w.token
                break

        if shop is not None and keyword is not None:
            # filter+regex表示模糊查询,输入的商铺名字可以不全,参数'i'表示不区分大小写
            e = u"?s :shopName ?name." \
                u"?s {rate} ?x." \
                u"filter regex(?name,'{shop}','i')".format(shop=w.token,rate=keyword)

            sparql = SPARQL_SELECT_TEM.format(prefix=SPARQL_PREXIX,
                                              select=select,
                                              expression=e)

        return sparql


class PropertyValueSet:
    def __init__(self):
        pass

    @staticmethod
    def return_general_rate_value():
        return u':shopAllRate'

    @staticmethod
    def return_quality_rate_value():
        return u':shopQualityRate'

    @staticmethod
    def return_service_rate_value():
        return u':shopServiceRate'

    @staticmethod
    def return_environment_rate_value():
        return u':shopEnvironmentRate'

# TODO 定义关键词

pos_shop = "nz"

shop_entity = (W(pos=pos_shop))

genre = (W("综合")|W("质量")|W("环境")|W("服务"))
general = (W("综合"))
quality = (W("质量"))
environment = (W("环境"))
service = (W("服务"))

higher = (W("大于") | W("高于"))
lower = (W("小于") | W("低于"))
compare = (higher | lower)

rating = (W("评分") | W("分") | W("分数"))

when = (W("何时") | W("时候"))
where = (W("哪里") | W("哪儿") | W("何地") | W("何处") | W("在") + W("哪"))

# TODO 问题模板/匹配规则
"""
1. 某商店在哪里
2. 某商店在几楼
3. 某类型的商店有哪些
4. 某楼层有哪些商店
5. 某商店的消费水平是多少
6. 某商店的某项评分是多少
"""
rules = [
    Rule(condition_num=2, condition=shop_entity +  Star(Any(), greedy=False) + where + Star(Any(), greedy=False),
         action=QuestionSet.shop_position_question),
    Rule(condition_num=3, condition=shop_entity +  Star(Any(), greedy=False) + genre + Star(Any(), greedy=False)
         + rating +Star(Any(), greedy=False) ,
         action=QuestionSet.shop_rating_question),
]

# TODO 具体的属性词匹配规则
genre_keyword_rules = [
    KeywordRule(condition=shop_entity + Star(Any(), greedy=False) + general + Star(Any(), greedy=False)
            + rating + Star(Any(), greedy=False), action=PropertyValueSet.return_general_rate_value),
    KeywordRule(condition=shop_entity + Star(Any(), greedy=False) + quality + Star(Any(), greedy=False)
            + rating + Star(Any(), greedy=False), action=PropertyValueSet.return_quality_rate_value),
    KeywordRule(condition=shop_entity + Star(Any(), greedy=False) + service + Star(Any(), greedy=False)
            + rating + Star(Any(), greedy=False), action=PropertyValueSet.return_service_rate_value),
    KeywordRule(condition=shop_entity + Star(Any(), greedy=False) + environment + Star(Any(), greedy=False)
            + rating + Star(Any(), greedy=False), action=PropertyValueSet.return_environment_rate_value)
]