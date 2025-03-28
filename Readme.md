# Better Minecart
Minecart Integrates Next-gen Enhancement Constructing Advanced Rapid Transportation

---M.I.N.E.C.A.R.T.---

Author: DuskRainFall

Test: DuskRainFall, Hell_Fraud

Thanks to 
- Bukkit/Paper API docs
- Minecraft Development for IntelliJ
- MyBatis docs CN ver.
- XLM server
- the others helped the development
## 插件效果
### 矿车
- 显示矿车速率：
  - 仅对玩家正在乘坐的矿车生效
  - 每隔`20`（有重力）/`10`（无重力）个载具移动事件之后，计算一次移动速率
  - 将平面/矢量速率和各向速率显示在乘客的`ActionBar`中
  - 在滑翔/飞行时平面速率小于`0.4`后会和速率显示交替占用`ActionBar`提示即将失速
  - 在降落时平面速率小于`0.4`后会和速率显示交替占用`ActionBar`提示即将降落
- 控制矿车极速：
  - 仅对玩家正在乘坐的矿车生效
  - 在矿车上主手持有回溯指针`RECOVERY_COMPASS`时：
    - 左键单击空气/交互方块可翻倍矿车单向极速
    - 右键单击空气可折半单向极速
    - 右键交互方块不会有效果
    - 并不会直接增加/减少矿车速率，而是限制矿车的速率上限
  - 极速调整有`10`刻冷却时间
  - 最大单向速率区间`0.05~1.5`格每刻（轨道）/`0.05~1.6`格每刻（飞行）
- 加强矿车强度：
  - 仅对玩家正在乘坐的矿车生效
  - 撞击方块时会尝试恢复原速度以避免上坡弹回（不影响正常撞击方块回弹）
  - 击飞碰撞到的一切非玩家载具实体
  - 对击飞的生物施加`动能失衡`效果
    - 虚弱4`WEAKNESS Ⅳ`
    - 缓慢4`SLOWNESS Ⅳ`
    - 反胃`NAUSEA`
  - 自身免疫撞击
  - 降低脱轨带来的速率损失
- 矿车滑翔功能：
  - 仅对玩家正在乘坐的矿车生效
  - 速度大小高于`0.6`的矿车将尝试进入`滑翔`状态
    - 为了防止错误的起飞，本格和下方不能是铁轨
    - 下方方块是空气则允许滑翔
    - 矿车将延迟一个和速率成反比的时间进入滑翔状态（这决定了初始下落速度的大小）
    - 如不腾空，判定为`滑行`状态以模拟起飞
  - `滑翔`：
    - 无视重力
    - 降低水平方向的飞行阻力
    - 通过动态调整竖直方向的飞行阻力模拟滑翔状态
    - 撞击方块时会退出滑翔状态并触发`坠机`
    - 滑翔状态的矿车在竖向速率大于`-0.2`时尝试进入`滑行`状态
      - 下方是空气则拒绝滑行
      - 本格或下方是铁轨拒绝进入`滑行`状态，而是直接退出滑翔状态
      - 其他：进入`滑行`状态
    - 滑翔状态的矿车没有进入滑行状态时会尝试退出滑翔状态
      - 下方是空气继续滑翔
      - 本格或下方是铁轨则退出滑翔状态
      - 下方是水时退出滑翔状态，延时等待入水后触发`坠机`
      - 其他：退出滑翔状态，触发`坠机`
  - `滑行`：
    - 飞行阻力修正：在平地/水面上方一格会迅速降低竖向速率以允许贴地/水面滑行
    - 速率达到`0.6`进行滑翔（复飞）
  - `坠机`：
    - 对乘客造成与速度正相关的伤害，并触发`120`游戏刻的点燃
    - 乘客将被弹出矿车，矿车实体将被移除
    - 尝试在落点周围创建持续时间为`1200`游戏刻的标准大小`温泉`
  - 平面速率低于`0.2`的滑翔矿车将进入`失速`状态
    - 重新受到重力影响
    - 飞行阻力复原
    - 下降率过快可能导致重新进入滑翔状态而触发`坠机`
  - 下车触发`弹射`
    - 矿车退出滑翔状态
    - 乘客获得缓降`SLOW_FALLING`
- 矿车飞行功能：
  - 仅对玩家正在乘坐的矿车生效
  - 在矿车上主手持有回溯指针`RECOVERY_COMPASS`时，可将`滑翔`转变为`飞行`状态
  - `飞行`：
    - 具有全部的滑翔特性
    - 矿车会向视角方向加速
      - 其水平加速度(x/z)与视线方向角有关
      - 其垂直加速度(y)与视线俯仰角有关
- 矿车航行功能：
  - 仅对玩家正在乘坐的矿车生效
  - 在`滑行`状态下接近/触碰水面可进入`航行`状态
  - `航行`：特殊的滑行状态
    - 具有全部的滑翔特性
    - 操纵方式同飞行
    - 航行状态下，拥有滑行阻力和可能的水阻力，因此阻力较大，不一定能达到极速
    - 可以完全没入水中
- 载具速度清零：可以通过`/vehicle stop`或其缩写`/ve s`将载具速度清零（滑翔/飞行不生效）
- 载具速度反向：可以通过`/vehicle back`或其缩写`/ve b`将载具速度反向（滑翔/飞行不生效）
### 船
- 显示船速率：
  - 仅对玩家正在乘坐的船生效
  - 每隔`20`个载具移动事件之后，计算一次移动速率
  - 将平面/矢量速率和各向速率显示在乘客的`ActionBar`中
- 加强船强度：
  - 仅对玩家正在乘坐的船生效
  - 击退碰撞到的非玩家载具和生物（无法击退轨道上的矿车）
  - 对击退的生物施加`动能失衡`效果
    - 虚弱4`WEAKNESS Ⅳ`
    - 缓慢4`SLOWNESS Ⅳ`
    - 反胃`NAUSEA`
### 温泉
- 温泉水可以提供
  - 抗性提升2`RESISTANCE Ⅱ`（蒸汽、水中生效）
  - 生命恢复2`REGENERATION Ⅱ`（蒸汽、水中生效）
  - 潮涌能量`CONDUIT_POWER`（仅水中）
- 温泉水具有`CLOUD`、`WHITE_SMOKE`、`BUBBLE`粒子效果
- 玩家位于水中时可向水中丢弃火焰弹`FIRE_CHARGE`
  - 以火焰弹爆裂点为中心创建一片无限时间的标准大小温泉水域（OP权限生效）
- 玩家位于水中时可向水中丢弃雪球`SNOWBALL`
  - 以雪球爆裂点为中心移除一片大型大小温泉水域（OP权限生效）
- 矿车在水上`坠机`时，以坠机点为中心创建一片持续时间为`1200`游戏刻的标准大小温泉水域
- 由火焰弹生成的温泉会在开启持久化后保存温泉方块到本地/指定的数据库，开机读取，而矿车的不会
  - 对于本地保存：直接将原集合转列表序列化，高速小巧
  - 对于数据库保存；直接保存每一个温泉方块，可能会对硬盘、加载/卸载速度造成一定影响，请注意数量
## 侵入物品
- 矿车 `MINECART` 实体
  - 玩家乘坐时
- 船 `BOAT` 实体
  - 玩家乘坐时
- 回溯指针`RECOVERY_COMPASS` 主手位物品
  - 占用 左键单击空气`LEFT_CLICK_AIR`
  - 占用 左键交互方块`LEFT_CLICK_BLOCK`
  - 占用 右键单击空气`RIGHT_CLICK_AIR`
- 火焰弹`FIRE_CHARGE` 掉落物
- 雪球`SNOWBALL` 掉落物
## 温泉持久化说明
在启动一次后，会在插件同级的插件同名文件夹下创建`.properties`文件
```properties
#不启用持久化
#savetype=null

#使用本地.dat序列化保存
#savetype=data

#使用mysql数据库保存
#savetype=mysql
```
- 默认`null`不保存
- `data`：使用POJO序列化直接将温泉方块列表保存到该文件同级`.dat`文件中
  - 高速保存，占用空间小，仅本地
- `mysql`：使用`MySQL`作为数据库管理系统，`MyBatis`作为持久层框架，可本地/远程数据库保存
  - 保存缓慢，占用空间较大，可远程
### 数据库表结构
本表保存一个单独服务器的温泉方块
```SQL
--主键不要选择自增，自增操作在后端完成
CREATE TABLE `数据库表名` (
  `id` bigint PRIMARY KEY,
  `world` varchar(20) NOT NULL,
  `x` double NOT NULL,
  `y` double NOT NULL,
  `z` double NOT NULL,
)
```
### MyBatis配置
- 该配置文件将在启动一次后自动在插件同级的插件同名文件夹下创建模板
- 配置文件`betterminecart-mybatis-config.xml`内容应该如下，中文内容需要修改为对应的参数
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <properties>
        <property name="tableName" value="数据库表名"/>
    </properties>
    <!-- 配置环境 -->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://数据库主机:数据库端口/数据库名?useSSL=false&amp;serverTimezone=UTC"/>
                <property name="username" value="用户名"/>
                <property name="password" value="密码"/>
            </dataSource>

        </environment>
    </environments>

    <!-- 加载映射文件 -->
    <mappers>
        <mapper resource="mapper/SpringBlocksMapper.xml"/>
    </mappers>

</configuration>
```
请务必注意不要填入和已有表名重复的值
## 测试相关
以下为测试中希望出现的情景

    + 代表需要查看效果
    - 代表在条件下不生效
    * 代表具有特殊情况
### 载具
+ 玩家交互监听
  - 主手不是`追溯指针`：不生效
  - 玩家不在载具内：不生效
  + 载具是载人矿车
    - 极速调整在`10刻`冷却中：不生效
    + 左击空气、左击方块
      + 取消原点击效果
      + 翻倍极速
        - 极速已经最大：不生效
        + 产生模拟加速的粒子和音效
        + 地面最大`1.5 block/tick`
        + 空中最大`1.6 block/tick`
    + 右击空气
      + 减半极速
      + 产生模拟减速的粒子和音效
      - 极速已经最小：不生效
      + 最小`0.05 block/tick`
    + 其他动作：清除冷却
  + 载具是船
    - 立体机动在`10刻`冷却中：不生效
    + 左击空气、左击方块
      + 取消原点击效果
     + 右击空气、右击方块
      + 船跳跃
        - 在地上：不生效
        - 在空中：不生效
        + 产生溅水音效
        + 增加竖直速度
+ 载具碰撞监听
  - 载具空：不生效
  - 载具上不是玩家：不生效
  + 碰撞方块
    + 对于载人矿车
      + 有重力（轨道/失速）
        + X/Z速度反向（此事件具有非期望行为）
          * 这对于上坡误撞击方块有效
          * 对正常在轨道上撞击方块无效
          * 这在飞行时可能造成一定的坠机判定失效
      + 无重力（滑翔/飞行/滑行/航行）
        + 停止飞行
          + 设为有重力
          + 空气阻力修改为原版
        + 爆炸
          + 对可被伤害乘客实体造成和单向最大速率正相关的伤害并给予`120刻`点燃
          + 弹出乘客
          + 产生模拟爆炸的粒子和音效
          + 创建`1200刻`的临时温泉
            + 产生模拟加热的粒子和音效
            + 将创建范围内方块加入集合
            + 构建`1200刻`的定时任务并加入任务队列
              + 将移除范围内方块移出集合（移除范围比创建范围更大）
              + 完成后将自己移出任务队列
          + 移除矿车
    + 对于船
      * 暂无
  + 碰撞实体
    - 对于撞击在载具中的实体：不生效
    + 对于载人矿车
      + 碰撞矿车
        - 碰撞在`20刻`冷却中：不生效
          + 一直碰撞则不会冷却
        + 播放碰撞生物音效
          - 在`10刻`冷却中：不生效
          + 一直碰撞则不会冷却
        + 为其设置更高的最大速率
        + 尝试将其速度直接翻倍（增加了限制）
        + 取消自身的被碰撞效果
        * 对于都有玩家的矿车相撞，则不会尝试修改其速度
      + 碰撞船
        - 碰撞在`20刻`冷却中：不生效
          + 一直碰撞则不会冷却
        + 播放碰撞生物音效
          - 在`10刻`冷却中：不生效
          + 一直碰撞则不会冷却
        + 将其速度翻倍，尤其是竖直方向
        + 取消自身的被碰撞效果
      + 碰撞生物
        - 碰撞在`20刻`冷却中：不生效
          + 一直碰撞则不会冷却
        + 播放碰撞生物音效
          - 在`10刻`冷却中：不生效
          + 一直碰撞则不会冷却
        + 将其速度翻倍，尤其是竖直方向（铁傀儡除外）
        + 为其施加被撞击的异常效果
        + 取消自身的被碰撞效果
        + 对于`铁傀儡`，矿车将爆炸
          + 对可被伤害乘客实体造成和单向最大速率正相关的伤害并给予`120刻`点燃
          + 弹出乘客
          + 产生模拟爆炸的粒子和音效
          + 创建`1200刻`的临时温泉
            + 产生模拟加热的粒子和音效
            + 将创建范围内方块加入集合
            + 构建`1200刻`的定时任务并加入任务队列
              + 将移除范围内方块移出集合（移除范围比创建范围更大）
              + 完成后将自己移出任务队列
          + 移除矿车
      + 碰撞其他实体
        + 将其速度翻倍，尤其是竖直方向
        + 取消自身的被碰撞效果
    + 对于船
      + 碰撞载具
        - 对于有人载具：不生效
        - 碰撞在`20刻`冷却中：不生效
          + 一直碰撞则不会冷却
        + 播放碰撞载具音效
          - 在`10刻`冷却中：不生效
          + 一直碰撞则不会冷却
        + 尝试为其增加两倍的船速度并将原速度反向，并增加额外的竖直速率
        * 对轨道上的矿车失效
      + 碰撞生物
        - 碰撞在`20刻`冷却中：不生效
          + 一直碰撞则不会冷却
        + 播放碰撞生物音效
          - 在`10刻`冷却中：不生效
          + 一直碰撞则不会冷却
        + 尝试为其增加两倍的船速度并将原速度反向，并增加额外的竖直速率
+ 离开载具监听
  - 载具不是矿车：不生效
  - 离开的不是玩家：不生效
  - 载具有重力：不生效
  + 矿车停止飞行
    + 设为有重力
    + 空气阻力修改为原版
  + 给予玩家缓降
+ 矿车移动监听
  + 监听1
    - 矿车空：不生效
    - 矿车上不是玩家：不生效
    + 有重力时`20`刻刷新一次，无重力则是`10`刻
    + 为矿车上所有玩家播报速率
    + 对不具有重力，且速率仅比失速速率大`0.2`的载具
      + 滑翔中：
        + 播报即将失速提示，和速率每`5刻`轮换一次
        + 播放警告音效
      + 滑行中：
        + 播报即将降落提示，和速率每`5刻`轮换一次
        + 播放滑行音效
  + 监听2
    - 矿车空：不生效
    - 矿车上不是玩家：不生效
    + 修改脱轨速率损失
    + 有重力（轨道/失速）
      + 大于起飞速度：尝试起飞
        + 检测本格方块
          - `铁轨`、`动力铁轨`、`侦测铁轨`：停飞
          + 检测下方方块
            - `铁轨`、`动力铁轨`、`侦测铁轨`：停飞
            + `空气`、`洞穴空气`：起飞
              + 延迟与速度成反比的时间：表现为初速度y向下的程度
              + 设为无重力
              + 修改空气阻力为飞行初始版
            + 其他方块：滑行
              + 修改空气阻力为滑行适用版
              + 大于起飞速度：起飞
                + 延迟与速度成反比的时间：表现为初速度y向下的程度
                + 设为无重力
                + 修改空气阻力为飞行初始版
              + 小于失速速度：停飞
                + 设为有重力
                + 空气阻力修改为原版
    + 无重力（滑翔/飞行/航行）
      + 小于失速速度
        + 停止飞行
          + 设为有重力
          + 空气阻力修改为原版
      + 查看空气阻力：
        + 非滑行状态：根据y的速度方向切换对应版本的适用空气阻力
        + 滑行状态：尝试起飞
      + 可进行飞行控制
        - 矿车上不是玩家：不生效
        - 未持有`追溯指针`：不生效
        + 检测玩家速度、方向角和俯仰角
          + 检测玩家方向角区间：以坐标轴为中心正负90°
          + 与对应的坐标轴夹角越小，那个方向的速度分量大小增长越快
          + 玩家竖向加速度由俯仰角大小决定
      + 尝试降落
        - 下降率大于`0.2`：不生效
        + 检测本格方块
          + `铁轨`、`动力铁轨`、`侦测铁轨`：停止飞行
            + 设为有重力
            + 空气阻力修改为原版
          + 其他
            + 检测下方方块
              - `空气`、`洞穴空气`：不生效
              + `铁轨`、`动力铁轨`、`侦测铁轨`：停止飞行
                + 设为有重力
                + 空气阻力修改为原版
              + 其他方块：滑行
                + 修改空气阻力为滑行适用版
                  + 大于起飞速度：起飞
                    + 延迟与速度成反比的时间：表现为初速度y向下的程度
                    + 设为无重力
                    + 修改空气阻力为飞行初始版
                  + 小于失速速度：停止飞行
                    + 设为有重力
                    + 空气阻力修改为原版
      + 若未滑行，尝试停止飞行
        + 检测本格方块
          + `铁轨`、`动力铁轨`、`侦测铁轨`：停止飞行
            + 设为有重力
            + 空气阻力修改为原版
        + 其他
          + 检测下方方块
            - `空气`、`洞穴空气`：不生效
            + `铁轨`、`动力铁轨`、`侦测铁轨`：停止飞行
              + 设为有重力
              + 空气阻力修改为原版
            + `水`：延迟`5刻`爆炸
              + 对可被伤害乘客实体造成和单向最大速率正相关的伤害并给予`120刻`点燃
              + 弹出乘客
              + 产生模拟爆炸的粒子和音效
              + 创建`1200刻`的临时温泉
                + 产生模拟加热的粒子和音效
                + 将创建范围内方块加入集合
                + 构建`1200刻`的定时任务并加入任务队列
                  + 将移除范围内方块移出集合（移除范围比创建范围更大）
                  + 完成后将自己移出任务队列
              + 移除矿车
            + 其他方块
              + 停止飞行
                + 设为有重力
                + 空气阻力修改为原版
              + 爆炸
                + 对可被伤害乘客实体造成和单向最大速率正相关的伤害并给予`120刻`点燃
                + 弹出乘客
                + 产生模拟爆炸的粒子和音效
                + 创建`1200刻`的临时温泉
                  + 产生模拟加热的粒子和音效
                  + 将创建范围内方块加入集合
                  + 构建`1200刻`的定时任务并加入任务队列
                    + 将移除范围内方块移出集合（移除范围比创建范围更大）
                    + 完成后将自己移出任务队列
+ 船移动监听
  + 监听1
    - 船空：不生效
    - 船上不是玩家：不生效
    + `20`刻刷新一次
    + 为船上所有玩家播报速率
+ 命令`/vehicle`（缩写`/ve`） 
  + 只能由命令发送者执行
  - 执行者不在载具中：不生效
  - 载具没有重力：不生效
  + 参数
    + `back`或`b`
      - 不是矿车或船：不生效
      + 反转速度方向
    + `stop`或`s`
      - 不是矿车或船：不生效
      + 速度置零
### 温泉
+ 物品丢弃监听
  - 玩家在`下界`：不生效
  - 玩家不在水中：不生效
  + 物品为`火焰弹`
    - 没有权限：不生效
    - 在冷却时间内：不生效
    + 创建永久温泉
      + 有`20`刻延迟
      - 尝试生成温泉时物品不在水中：不生效
      + 产生模拟加热的粒子和音效
      + 将创建范围内方块加入集合
      + 移除物品
  + 物品为`雪球`
    - 没有权限：不生效
    - 在冷却时间内：不生效
    + 移除温泉
      + 有`20`刻延迟
      - 尝试生成温泉时物品不在水中：不生效
      + 产生模拟降温的粒子和音效
      + 将移除范围内方块移出集合
      + 移除物品
### 插件
+ 在插件加载时
  + 尝试在插件同路径下创建`betterminecart`文件夹
    + 尝试初始化`betterminecart.properties`文件
    + 尝试初始化`betterminecart-mybatis-config.xml`文件模板
  + 检查`betterminecart.properties`中`savetype`的值
    - `null`持久化不生效
    + `data`启用`.dat`本地存储
      + 尝试从`betterminecart.dat`中反序列化对象
    + `mysql`启用数据库存储
      + 尝试加载配置文件`betterminecart-mybatis-config.xml`
        + 配置文件正常
          + 数据库表不存在：尝试根据配置的表名自动创建数据库表
          + 数据库表存在：可以从对应数据库正确读出温泉方块
        - 配置文件不存在/格式异常/数据异常：报错
  + 温泉方块开始产生模拟温泉的粒子
+ 在插件卸载时
  + 若启用温泉方块持久化
    + 遍历限时温泉列表，依次强制移除
    + 对于`data`：尝试创建`.dat`文件并保存
    + 对于`mysql`：保存温泉方块到对应数据库
### 对于原版的侵入控制
尽量做到，但难以保证

+ 在正常装载生物时，载具表现应和原版一致
+ 在不使用追溯指针操作时，载具表现应和原版一致
+ 在正常铁轨上运行时，应当只是更高速的矿车
* 未修复：速度超过一定值后（不小于0.4格每刻），进入弯道有可能会脱轨（脱轨时机和方向不定）
### 其他
+ 如果你觉得这不像矿车/滑翔机，那它就应该优化