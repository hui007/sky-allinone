--[[
for i = 1, 100
do 
    redis.call("set", i..KEYS[1], i..ARGV[1])
end 
--]]


-- 读取源集合里的数据
local sourceEles = redis.call("ZRANGE", KEYS[1], ARGV[1], ARGV[2], 'withscores')

if( #sourceEles == 0 )
then
   --[ 如果条件为真则输出如下内容　--]
   print("111")
   print(sourceEles)
   return sourceEles
end

-- 放入目标集合
for j=1,#sourceEles,2 do
      redis.call("ZADD", KEYS[2], tonumber(sourceEles[j+1]), sourceEles[j])
end

-- 删除源集合里的数据
redis.call("ZREMRANGEBYRANK", KEYS[1], ARGV[1], ARGV[2])

-- 返回源集合里的数据
return sourceEles