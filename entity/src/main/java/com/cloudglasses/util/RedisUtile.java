package com.cloudglasses.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Redis工具类
 *
 * @author WangLei
 * @date 2018 -08-21 09:58:49
 */
@Component
@Slf4j
public class RedisUtile {
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 出异常，重复操作的次数
     */
    private static Integer times = 5;


    /**
     * 获取Redis中所有的键的key
     *
     * @return the all keys
     * @author WangLei
     * @date 2018 -08-21 09:58:49
     */
    public Set<String> getAllKeys() {
        return redisTemplate.keys("*");
    }


    /**
     * 获取所有的普通key-value
     *
     * @return the all string
     * @author WangLei
     * @date 2018 -08-21 09:58:49
     */
    public Map<String, Object> getAllString() {
        Set<String> stringSet = getAllKeys();
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.STRING) {
                map.put(k, get(k));
            }
        }
        return map;
    }


    /**
     * 获取所有的Set -key-value
     *
     * @return the all set
     * @author WangLei
     * @date 2018 -08-21 09:58:49
     */
    public Map<String, Set<Object>> getAllSet() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<String, Set<Object>>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.SET) {
                map.put(k, getSet(k));
            }
        }
        return map;
    }


    /**
     * 获取所有的ZSet正序  -key-value 不获取权重值
     *
     * @return the all z set range
     * @author WangLei
     * @date 2018 -08-21 09:58:50
     */
    public Map<String, Set<Object>> getAllZSetRange() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<String, Set<Object>>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.ZSET) {
                log.debug("k:" + k);
                map.put(k, getZSetRange(k));
            }
        }
        return map;
    }


    /**
     * 获取所有的ZSet倒序  -key-value 不获取权重值
     *
     * @return the all z set reverse range
     * @author WangLei
     * @date 2018 -08-21 09:58:50
     */
    public Map<String, Set<Object>> getAllZSetReverseRange() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<String, Set<Object>>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.ZSET) {
                map.put(k, getZSetReverseRange(k));
            }
        }
        return map;
    }


    /**
     * 获取所有的List -key-value
     *
     * @return the all list
     * @author WangLei
     * @date 2018 -08-21 09:58:50
     */
    public Map<String, List<Object>> getAllList() {
        Set<String> stringSet = getAllKeys();
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.LIST) {
                map.put(k, getList(k));
            }
        }
        return map;
    }


    /**
     * 获取所有的Map -key-value
     *
     * @return the all map
     * @author WangLei
     * @date 2018 -08-21 09:58:50
     */
    public Map<String, Map<String, Object>> getAllMap() {
        Set<String> stringSet = getAllKeys();
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.HASH) {
                map.put(k, getMap(k));
            }
        }
        return map;
    }


    /**
     * 添加一个list
     *
     * @param key
     *         the key
     * @param objectList
     *         the object list
     * @author WangLei
     * @date 2018 -08-21 09:58:50
     */
    public void addList(String key, List<Object> objectList) {
        for (Object obj : objectList) {
            addList(key, obj);
        }
    }


    /**
     * 向list中增加值
     *
     * @param key
     *         the key
     * @param obj
     *         the obj
     * @return 返回在list中的下标
     * @author WangLei
     * @date 2018 -08-21 09:58:50
     */
    public long addList(String key, Object obj) {
        return redisTemplate.boundListOps(key).rightPush(obj);
    }


    /**
     * 向list中增加值
     *
     * @param key
     *         the key
     * @param obj
     *         the obj
     * @return 返回在list中的下标
     * @author WangLei
     * @date 2018 -08-21 09:58:50
     */
    public long addList(String key, Object... obj) {
        return redisTemplate.boundListOps(key).rightPushAll(obj);
    }


    /**
     * 输出list
     *
     * @param key
     *         List的key
     * @param s
     *         开始下标
     * @param e
     *         结束的下标
     * @return the list
     * @author WangLei
     * @date 2018 -08-21 09:58:50
     */
    public List<Object> getList(String key, long s, long e) {
        return redisTemplate.boundListOps(key).range(s, e);
    }


    /**
     * 输出完整的list
     *
     * @param key
     *         the key
     * @return the list
     * @author WangLei
     * @date 2018 -08-21 09:58:50
     */
    public List<Object> getList(String key) {
        return redisTemplate.boundListOps(key).range(0, getListSize(key));
    }


    /**
     * 获取list集合中元素的个数
     *
     * @param key
     *         the key
     * @return the list size
     * @author WangLei
     * @date 2018 -08-21 09:58:50
     */
    public long getListSize(String key) {
        return redisTemplate.boundListOps(key).size();
    }


    /**
     * 移除list中某值
     * 移除list中 count个value为object的值,并且返回移除的数量,
     * 如果count为0,或者大于list中为value为object数量的总和,
     * 那么移除所有value为object的值,并且返回移除数量
     *
     * @param key
     *         the key
     * @param object
     *         the object
     * @return 返回移除数量
     * @author WangLei
     * @date 2018 -08-21 09:58:51
     */
    public long removeListValue(String key, Object object) {
        return redisTemplate.boundListOps(key).remove(0, object);
    }


    /**
     * 移除list中某值
     *
     * @param key
     *         the key
     * @param objects
     *         the objects
     * @return 返回移除数量
     * @author WangLei
     * @date 2018 -08-21 09:58:51
     */
    public long removeListValue(String key, Object... objects) {
        long r = 0;
        for (Object object : objects) {
            r += removeListValue(key, object);
        }
        return r;
    }


    /**
     * 批量删除key对应的value
     *
     * @param key
     *         the key
     * @author WangLei
     * @date 2018 -08-21 09:58:51
     */
    public void remove(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                remove(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }


    /**
     * 批量模糊移除
     * 支持*号等匹配移除
     *
     * @param blears
     *         the blears
     * @author WangLei
     * @date 2018 -08-21 09:58:51
     */
    public void removeBlear(String... blears) {
        for (String blear : blears) {
            removeBlear(blear);
        }
    }


    /**
     * 修改key名 如果不存在该key或者没有修改成功返回false
     *
     * @param oldKey
     *         the old key
     * @param newKey
     *         the new key
     * @return the boolean
     * @author WangLei
     * @date 2018 -08-21 09:58:51
     */
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }


    /**
     * 模糊移除
     * 支持*号等匹配移除
     *
     * @param blear
     *         the blear
     * @author WangLei
     * @date 2018 -08-21 09:58:51
     */
    public void removeBlear(String blear) {
        redisTemplate.delete(redisTemplate.keys(blear));
    }


    /**
     * 根据正则表达式来移除key-value
     *
     * @param blears
     *         the blears
     * @author WangLei
     * @date 2018 -08-21 09:58:51
     */
    public void removeByRegular(String... blears) {
        for (String blear : blears) {
            removeBlear(blear);
        }
    }


    /**
     * 根据正则表达式来移除key-value
     *
     * @param blear
     *         the blear
     * @author WangLei
     * @date 2018 -08-21 09:58:51
     */
    public void removeByRegular(String blear) {
        Set<String> stringSet = getAllKeys();
        for (String s : stringSet) {
            if (Pattern.compile(blear).matcher(s).matches()) {
                redisTemplate.delete(s);
            }
        }
    }


    /**
     * 根据正则表达式来移除 Map中的key-value
     *
     * @param key
     *         the key
     * @param blears
     *         the blears
     * @author WangLei
     * @date 2018 -08-21 09:58:51
     */
    public void removeMapFieldByRegular(String key, String... blears) {
        for (String blear : blears) {
            removeMapFieldByRegular(key, blear);
        }
    }


    /**
     * 根据正则表达式来移除 Map中的key-value
     *
     * @param key
     *         the key
     * @param blear
     *         the blear
     * @author WangLei
     * @date 2018 -08-21 09:58:52
     */
    public void removeMapFieldByRegular(String key, String blear) {
        Map<String, Object> map = getMap(key);
        Set<String> stringSet = map.keySet();
        for (String s : stringSet) {
            if (Pattern.compile(blear).matcher(s).matches()) {
                redisTemplate.boundHashOps(key).delete(s);
            }
        }
    }


    /**
     * 移除key 对应的value
     *
     * @param key
     *         the key
     * @param value
     *         the value
     * @return the long
     * @author WangLei
     * @date 2018 -08-21 09:58:52
     */
    public Long removeZSetValue(String key, Object... value) {
        return redisTemplate.boundZSetOps(key).remove(value);
    }


    /**
     * 移除key ZSet
     *
     * @param key
     *         the key
     * @author WangLei
     * @date 2018 -08-21 09:58:52
     */
    public void removeZSet(String key) {
        removeZSetRange(key, 0L, getZSetSize(key));
    }


    /**
     * 删除，键为K的集合，索引start<=index<=end的元素子集
     *
     * @param key
     *         the key
     * @param start
     *         the start
     * @param end
     *         the end
     * @author WangLei
     * @date 2018 -08-21 09:58:52
     */
    public void removeZSetRange(String key, Long start, Long end) {
        redisTemplate.boundZSetOps(key).removeRange(start, end);
    }


    /**
     * 并集 将key对应的集合和key1对应的集合合并到key2中
     * 如果分数相同的值，都会保留
     * 原来key2的值会被覆盖
     *
     * @param key
     *         the key
     * @param key1
     *         the key 1
     * @param key2
     *         the key 2
     * @author WangLei
     * @date 2018 -08-21 09:58:52
     */
    public void setZSetUnionAndStore(String key, String key1, String key2) {
        redisTemplate.boundZSetOps(key).unionAndStore(key1, key2);
    }


    /**
     * 获取整个有序集合ZSET，正序
     *
     * @param key
     *         the key
     * @return the z set range
     * @author WangLei
     * @date 2018 -08-21 09:58:52
     */
    public Set<Object> getZSetRange(String key) {
        return getZSetRange(key, 0, getZSetSize(key));
    }


    /**
     * 获取有序集合ZSET
     * 键为K的集合，索引start<=index<=end的元素子集，正序
     *
     * @param key
     *         the key
     * @param s
     *         the s
     * @param e
     *         the e
     * @return the z set range
     * @author WangLei
     * @date 2018 -08-21 09:58:52
     */
    public Set<Object> getZSetRange(String key, long s, long e) {
        return redisTemplate.boundZSetOps(key).range(s, e);
    }


    /**
     * 获取整个有序集合ZSET，倒序
     *
     * @param key
     *         the key
     * @return the z set reverse range
     * @author WangLei
     * @date 2018 -08-21 09:58:52
     */
    public Set<Object> getZSetReverseRange(String key) {
        return getZSetReverseRange(key, 0, getZSetSize(key));
    }


    /**
     * 获取有序集合ZSET
     * 键为K的集合，索引start<=index<=end的元素子集，倒序
     *
     * @param key
     *         the key
     * @param start
     *         the start
     * @param end
     *         the end
     * @return the z set reverse range
     * @author WangLei
     * @date 2018 -08-21 09:58:52
     */
    public Set<Object> getZSetReverseRange(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).reverseRange(start, end);
    }


    /**
     * 通过分数(权值)获取ZSET集合 正序 -从小到大
     *
     * @param key
     *         the key
     * @param start
     *         the start
     * @param end
     *         the end
     * @return the z set range by score
     * @author WangLei
     * @date 2018 -08-21 09:58:52
     */
    public Set<Object> getZSetRangeByScore(String key, double start, double end) {
        return redisTemplate.boundZSetOps(key).rangeByScore(start, end);
    }

    /**
     * 通过分数(权值)获取ZSET集合 倒序 -从大到小
     *
     * @param key
     *         the key
     * @param start
     *         the start
     * @param end
     *         the end
     * @return the z set reverse range by score
     * @author WangLei
     * @date 2018 -08-21 09:58:52
     */
    public Set<Object> getZSetReverseRangeByScore(String key, double start, double end) {
        return redisTemplate.boundZSetOps(key).reverseRangeByScore(start, end);
    }


    /**
     * 键为K的集合，索引start<=index<=end的元素子集
     * 返回泛型接口（包括score和value），正序
     *
     * @param key
     *         the key
     * @param start
     *         the start
     * @param end
     *         the end
     * @return the z set range with scores
     * @author WangLei
     * @date 2018 -08-21 09:58:53
     */
    public Set<ZSetOperations.TypedTuple<Object>> getZSetRangeWithScores(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).rangeWithScores(start, end);
    }


    /**
     * 键为K的集合，索引start<=index<=end的元素子集
     * 返回泛型接口（包括score和value），倒序
     *
     * @param key
     *         the key
     * @param start
     *         the start
     * @param end
     *         the end
     * @return the z set reverse range with scores
     * @author WangLei
     * @date 2018 -08-21 09:58:53
     */
    public Set<ZSetOperations.TypedTuple<Object>> getZSetReverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).reverseRangeWithScores(start, end);
    }


    /**
     * 键为K的集合
     * 返回泛型接口（包括score和value），正序
     *
     * @param key
     *         the key
     * @return the z set range with scores
     * @author WangLei
     * @date 2018 -08-21 09:58:53
     */
    public Set<ZSetOperations.TypedTuple<Object>> getZSetRangeWithScores(String key) {
        return getZSetRangeWithScores(key, 0, getZSetSize(key));
    }


    /**
     * 键为K的集合
     * 返回泛型接口（包括score和value），倒序
     *
     * @param key
     *         the key
     * @return the z set reverse range with scores
     * @author WangLei
     * @date 2018 -08-21 09:58:53
     */
    public Set<ZSetOperations.TypedTuple<Object>> getZSetReverseRangeWithScores(String key) {
        return getZSetReverseRangeWithScores(key, 0, getZSetSize(key));
    }


    /**
     * 键为K的集合，sMin<=score<=sMax的元素个数
     *
     * @param key
     *         the key
     * @param sMin
     *         the s min
     * @param sMax
     *         the s max
     * @return the z set count size
     * @author WangLei
     * @date 2018 -08-21 09:58:53
     */
    public long getZSetCountSize(String key, double sMin, double sMax) {
        return redisTemplate.boundZSetOps(key).count(sMin, sMax);
    }


    /**
     * 获取Zset 键为K的集合元素个数
     *
     * @param key
     *         the key
     * @return the z set size
     * @author WangLei
     * @date 2018 -08-21 09:58:53
     */
    public long getZSetSize(String key) {
        return redisTemplate.boundZSetOps(key).size();
    }


    /**
     * 获取键为K的集合，value为obj的元素分数
     *
     * @param key
     *         the key
     * @param value
     *         the value
     * @return the z set score
     * @author WangLei
     * @date 2018 -08-21 09:58:53
     */
    public double getZSetScore(String key, Object value) {
        return redisTemplate.boundZSetOps(key).score(value);
    }


    /**
     * 元素分数增加，delta是增量
     *
     * @param key
     *         the key
     * @param value
     *         the value
     * @param delta
     *         the delta
     * @return the double
     * @author WangLei
     * @date 2018 -08-21 09:58:53
     */
    public double incrementZSetScore(String key, Object value, double delta) {
        return redisTemplate.boundZSetOps(key).incrementScore(value, delta);
    }


    /**
     * 添加有序集合ZSET
     * 默认按照score升序排列，存储格式K(1)==V(n)，V(1)=S(1)
     *
     * @param key
     *         the key
     * @param score
     *         the score
     * @param value
     *         the value
     * @return the boolean
     * @author WangLei
     * @date 2018 -08-21 09:58:53
     */
    public Boolean addZSet(String key, double score, Object value) {
        return redisTemplate.boundZSetOps(key).add(value, score);
    }


    /**
     * 添加有序集合ZSET
     *
     * @param key
     *         the key
     * @param value
     *         the value
     * @return the long
     * @author WangLei
     * @date 2018 -08-21 09:58:54
     */
    public Long addZSet(String key, TreeSet<Object> value) {
        return redisTemplate.boundZSetOps(key).add(value);
    }


    /**
     * 添加有序集合ZSET
     *
     * @param key
     *         the key
     * @param score
     *         the score
     * @param value
     *         the value
     * @return the boolean
     * @author WangLei
     * @date 2018 -08-21 09:58:54
     */
    public Boolean addZSet(String key, double[] score, Object[] value) {
        if (score.length != value.length) {
            return false;
        }
        for (int i = 0; i < score.length; i++) {
            if (addZSet(key, score[i], value[i]) == false) {
                return false;
            }
        }
        return true;
    }


    /**
     * 删除缓存
     * 根据key精确匹配删除
     *
     * @param key
     *         the key
     * @author WangLei
     * @date 2018 -08-21 09:58:54
     */
    public void remove(String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }


    /**
     * 通过分数删除ZSet中的值
     *
     * @param key
     *         the key
     * @param s
     *         the s
     * @param e
     *         the e
     * @author WangLei
     * @date 2018 -08-21 09:58:54
     */
    public void removeZSetRangeByScore(String key, double s, double e) {
        redisTemplate.boundZSetOps(key).removeRangeByScore(s, e);
    }


    /**
     * 设置Set的过期时间
     *
     * @param key
     *         the key
     * @param time
     *         the time
     * @return the set expire time
     * @author WangLei
     * @date 2018 -08-21 09:58:54
     */
    public Boolean setSetExpireTime(String key, Long time) {
        return redisTemplate.boundSetOps(key).expire(time, TimeUnit.SECONDS);
    }


    /**
     * 设置ZSet的过期时间
     *
     * @param key
     *         the key
     * @param time
     *         the time
     * @return the z set expire time
     * @author WangLei
     * @date 2018 -08-21 09:58:54
     */
    public Boolean setZSetExpireTime(String key, Long time) {
        return redisTemplate.boundZSetOps(key).expire(time, TimeUnit.SECONDS);
    }


    /**
     * 判断缓存中是否有key对应的value
     *
     * @param key
     *         the key
     * @return the boolean
     * @author WangLei
     * @date 2018 -08-21 09:58:54
     */
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取String缓存 可以是对象
     *
     * @param key
     *         the key
     * @return the object
     * @author WangLei
     * @date 2018 -08-21 09:58:54
     */
    public <T> T get(int key) {
        return this.get(String.valueOf(key));
    }

    /**
     * 读取String缓存 可以是对象
     *
     * @param key
     *         the key
     * @return the object
     * @author WangLei
     * @date 2018 -08-21 09:58:54
     */
    public <T> T get(long key) {
        return this.get(String.valueOf(key));
    }

    /**
     * 读取String缓存 可以是对象
     *
     * @param key
     *         the key
     * @return the object
     * @author WangLei
     * @date 2018 -08-21 09:58:54
     */
    public <T> T get(String key) {
        return (T) redisTemplate.boundValueOps(key).get();
    }


    /**
     * 读取String缓存 可以是对象
     *
     * @param keys
     *         the keys
     * @return the list
     * @author WangLei
     * @date 2018 -08-21 09:58:55
     */
    public List<Object> get(String... keys) {
        List<Object> list = new ArrayList<Object>();
        for (String key : keys) {
            list.add(get(key));
        }
        return list;
    }


    /**
     * 读取缓存 可以是对象 根据正则表达式匹配
     *
     * @param regKey
     *         the reg key
     * @return the by regular
     * @author WangLei
     * @date 2018 -08-21 09:58:55
     */
    public List<Object> getByRegular(String regKey) {
        Set<String> stringSet = getAllKeys();
        List<Object> objectList = new ArrayList<Object>();
        for (String s : stringSet) {
            if (Pattern.compile(regKey).matcher(s).matches() && getType(s) == DataType.STRING) {
                objectList.add(get(s));
            }
        }
        return objectList;
    }


    /**
     * 写入缓存 可以是对象
     *
     * @param key
     *         the key
     * @param value
     *         the value
     * @author WangLei
     * @date 2018 -08-21 09:58:55
     */
    public void set(long key, Object value) {
        this.set(String.valueOf(key), value);
    }

    /**
     * 写入缓存 可以是对象
     *
     * @param key
     *         the key
     * @param value
     *         the value
     * @author WangLei
     * @date 2018 -08-21 09:58:55
     */
    public void set(int key, Object value) {
        this.set(String.valueOf(key), value);
    }

    /**
     * 写入缓存 可以是对象
     *
     * @param key
     *         the key
     * @param value
     *         the value
     * @author WangLei
     * @date 2018 -08-21 09:58:55
     */
    public void set(String key, Object value) {
        redisTemplate.boundValueOps(key).set(value);
    }


    /**
     * 写入缓存
     *
     * @param key
     *         the key
     * @param value
     *         the value
     * @param expireTime
     *         过期时间 -单位s
     * @author WangLei
     * @date 2018 -08-21 09:58:55
     */
    public void set(String key, Object value, Long expireTime) {
        redisTemplate.boundValueOps(key).set(value, expireTime, TimeUnit.SECONDS);
    }


    /**
     * 设置一个key的过期时间（单位：秒）
     *
     * @param key
     *         the key
     * @param expireTime
     *         the expire time
     * @return the expire time
     * @author WangLei
     * @date 2018 -08-21 09:58:55
     */
    public boolean setExpireTime(String key, Long expireTime) {
        return redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }


    /**
     * 获取key的类型
     *
     * @param key
     *         the key
     * @return the type
     * @author WangLei
     * @date 2018 -08-21 09:58:55
     */
    public DataType getType(String key) {
        return redisTemplate.type(key);
    }


    /**
     * 删除map中的某个对象
     *
     * @param key
     *         map对应的key
     * @param field
     *         map中该对象的key
     * @author WangLei
     * @date 2018 -08-21 09:58:55
     */
    public void removeMapField(String key, Object... field) {
        redisTemplate.boundHashOps(key).delete(field);
    }


    /**
     * 获取map对象大小
     *
     * @param key
     *         map对应的key
     * @return the map size
     * @author WangLei
     * @date 2018 -08-21 09:58:55
     */
    public Long getMapSize(String key) {
        return redisTemplate.boundHashOps(key).size();
    }


    /**
     * 获取map对象
     *
     * @param key
     *         the key
     * @return the map
     * @author WangLei
     * @date 2018 -08-21 09:58:56
     */
    public Map<String, Object> getMap(String key) {
        return redisTemplate.boundHashOps(key).entries();
    }


    /**
     * 获取map缓存中的某个对象
     *
     * @param <T>
     *         the type parameter
     * @param key
     *         map对应的key
     * @param field
     *         map中该对象的key
     * @return the map field
     * @author WangLei
     * @date 2018 -08-21 09:58:56
     */
    public <T> T getMapField(String key, String field) {
        return (T) redisTemplate.boundHashOps(key).get(field);
    }


    /**
     * 判断map中对应key的key是否存在
     *
     * @param key
     *         map对应的key
     * @param field
     *         map中该对象的key
     * @return the boolean
     * @author WangLei
     * @date 2018 -08-21 09:58:56
     */
    public Boolean hasMapKey(String key, String field) {
        return redisTemplate.boundHashOps(key).hasKey(field);
    }


    /**
     * 获取map对应key的value
     *
     * @param key
     *         the key
     * @return the map field value
     * @author WangLei
     * @date 2018 -08-21 09:58:56
     */
    public List<Object> getMapFieldValue(String key) {
        return redisTemplate.boundHashOps(key).values();
    }


    /**
     * 获取map的key
     *
     * @param key
     *         the key
     * @return the map field key
     * @author WangLei
     * @date 2018 -08-21 09:58:56
     */
    public Set<Object> getMapFieldKey(String key) {
        return redisTemplate.boundHashOps(key).keys();
    }


    /**
     * 添加map
     *
     * @param key
     *         the key
     * @param map
     *         the map
     * @author WangLei
     * @date 2018 -08-21 09:58:56
     */
    public void addMap(String key, Map<String, Object> map) {
        redisTemplate.boundHashOps(key).putAll(map);
    }


    /**
     * 向key对应的map中添加缓存对象
     *
     * @param key
     *         map对应的key
     * @param field
     *         map中该对象的key
     * @param value
     *         值
     * @author WangLei
     * @date 2018 -08-21 09:58:56
     */
    public void addMap(String key, String field, Object value) {
        redisTemplate.boundHashOps(key).put(field, value);
    }


    /**
     * 向key对应的map中添加缓存对象
     *
     * @param key
     *         map对应的key
     * @param field
     *         map中该对象的key
     * @param value
     *         值
     * @param time
     *         过期时间-整个MAP的过期时间
     * @author WangLei
     * @date 2018 -08-21 09:58:56
     */
    public void addMap(String key, String field, Object value, long time) {
        redisTemplate.boundHashOps(key).put(field, value);
        redisTemplate.boundHashOps(key).expire(time, TimeUnit.SECONDS);
    }


    /**
     * 处理事务时锁定key
     *
     * @param key
     *         the key
     * @author WangLei
     * @date 2018 -08-21 09:58:56
     */
    public void watch(String key) {
        redisTemplate.watch(key);
    }


    /**
     * 向set中加入对象
     *
     * @param key
     *         对象key
     * @param obj
     *         值
     * @author WangLei
     * @date 2018 -08-21 09:58:56
     */
    public void addSet(String key, Object... obj) {
        redisTemplate.boundSetOps(key).add(obj);
    }


    /**
     * 移除set中的某些值
     *
     * @param key
     *         the key
     * @param obj
     *         the obj
     * @return the long
     * @author WangLei
     * @date 2018 -08-21 09:58:56
     */
    public long removeSetValue(String key, Object obj) {
        return redisTemplate.boundSetOps(key).remove(obj);
    }


    /**
     * 移除set中的某些值
     *
     * @param key
     *         the key
     * @param obj
     *         the obj
     * @return the long
     * @author WangLei
     * @date 2018 -08-21 09:58:56
     */
    public long removeSetValue(String key, Object... obj) {
        if (obj != null && obj.length > 0) {
            return redisTemplate.boundSetOps(key).remove(obj);
        }
        return 0L;
    }


    /**
     * 获取set的对象数
     *
     * @param key
     *         the key
     * @return the set size
     * @author WangLei
     * @date 2018 -08-21 09:58:56
     */
    public long getSetSize(String key) {
        return redisTemplate.boundSetOps(key).size();
    }


    /**
     * 判断set中是否存在这个值
     *
     * @param key
     *         the key
     * @param obj
     *         the obj
     * @return the boolean
     * @author WangLei
     * @date 2018 -08-21 09:58:57
     */
    public Boolean hasSetValue(String key, Object obj) {
        Boolean boo = null;
        int t = 0;
        while (true) {
            try {
                boo = redisTemplate.boundSetOps(key).isMember(obj);
                break;
            } catch (Exception e) {
                log.error("key[" + key + "],obj[" + obj + "]判断Set中的值是否存在失败,异常信息:" + e.getMessage());
                t++;
            }
            if (t > times) {
                break;
            }
        }
        log.info("key[" + key + "],obj[" + obj + "]是否存在,boo:" + boo);
        return boo;
    }


    /**
     * 获得整个set
     *
     * @param key
     *         the key
     * @return the set
     * @author WangLei
     * @date 2018 -08-21 09:58:57
     */
    public Set<Object> getSet(String key) {
        return redisTemplate.boundSetOps(key).members();
    }


    /**
     * 获得set 并集
     *
     * @param key
     *         the key
     * @param otherKey
     *         the other key
     * @return the set union
     * @author WangLei
     * @date 2018 -08-21 09:58:57
     */
    public Set<Object> getSetUnion(String key, String otherKey) {
        return redisTemplate.boundSetOps(key).union(otherKey);
    }


    /**
     * 获得set 并集
     *
     * @param key
     *         the key
     * @param set
     *         the set
     * @return the set union
     * @author WangLei
     * @date 2018 -08-21 09:58:57
     */
    public Set<Object> getSetUnion(String key, Set<Object> set) {
        return redisTemplate.boundSetOps(key).union(set);
    }


    /**
     * 获得set 交集
     *
     * @param key
     *         the key
     * @param otherKey
     *         the other key
     * @return the set intersect
     * @author WangLei
     * @date 2018 -08-21 09:58:57
     */
    public Set<Object> getSetIntersect(String key, String otherKey) {
        return redisTemplate.boundSetOps(key).intersect(otherKey);
    }


    /**
     * 获得set 交集
     *
     * @param key
     *         the key
     * @param set
     *         the set
     * @return the set intersect
     * @author WangLei
     * @date 2018 -08-21 09:58:57
     */
    public Set<Object> getSetIntersect(String key, Set<Object> set) {
        return redisTemplate.boundSetOps(key).intersect(set);
    }
}
