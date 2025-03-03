# Better Minecart
Author: DuskRainFall
## 插件效果
### 载具
- 显示载具速率：
  - 仅对玩家正在乘坐的载具生效
  - 每隔`20`个载具移动事件之后，计算一次载具移动速率
  - 将矢量速率和各向速率显示在乘客的`ActionBar`中
  - 在滑翔/飞行时速率小于`0.4`后会和速率显示交替占用`ActionBar`提示即将失速
- 控制矿车极速：
  - 仅对玩家正在乘坐的矿车生效
  - 在矿车上主手持有回溯指针`RECOVERY_COMPASS`时：
    - 左键单击空气/交互方块可翻倍矿车单向极速，出现`ENTITY_FIREWORK_ROCKET_LAUNCH`音效和`FIREWORK`粒子效果
    - 右键单击空气可折半单向极速，出现`BLOCK_ANVIL_HIT`音效和`LAVA`粒子效果
    - 右键交互方块不会有效果
    - 并不会直接增加/减少矿车速率，而是限制矿车的速率上限
  - 最大单向速率区间`0.05~1.5`格每刻（轨道）/`0.05~1.6`格每刻（飞行）
- 加强矿车强度：
  - 仅对玩家正在乘坐的矿车生效
  - 撞击方块时会尝试恢复原速度以避免上坡弹回（不影响正常撞击方块回弹）
  - 击飞碰撞到的一切实体
  - 对击飞的生物施加`动能失衡`效果
    - 虚弱4`WEAKNESS Ⅳ`
    - 缓慢4`SLOWNESS Ⅳ`
    - 反胃`NAUSEA`
  - 自身免疫撞击
  - 降低脱轨带来的速率损失
- 矿车滑翔功能：
  - 仅对玩家正在乘坐的矿车生效
  - 矢量速率高于`0.6`的矿车将尝试进入`滑翔`状态
    - 检查下方方块：是空气`AIR`或洞穴空气`CAVE_AIR`则判定为腾空，允许滑翔
    - 矿车将延迟一个和速率成反比的时间进入滑翔状态（这决定了初始下落速度的大小）
    - 如不腾空，判定为`降落`状态
  - `滑翔`：
    - 无视重力
    - 降低水平方向的飞行阻力
    - 通过动态调整竖直方向的飞行阻力模拟滑翔状态
    - 撞击方块时会退出滑翔状态并触发`坠机`
    - 滑翔状态的矿车在竖向速率大于`-0.2`时会检查下方方块尝试进入`降落`状态
      - 空气`AIR`、洞穴空气`CAVE_AIR`：拒绝进入`降落`状态
      - 铁轨`RAIL`、探测铁轨`DETECTOR_RAIL`、动力铁轨`POWERED_RAIL`：拒绝进入`降落`状态，直接退出滑翔状态
      - 其他：进入`降落`状态
    - 滑翔状态的矿车没有进入降落状态时会检查下方方块尝试退出滑翔状态
      - 空气`AIR`、洞穴空气`CAVE_AIR`：继续滑翔
      - 铁轨`RAIL`、探测铁轨`DETECTOR_RAIL`、动力铁轨`POWERED_RAIL`：退出滑翔状态
      - 水`WATER`：退出滑翔状态，延时等待入水后触发`坠机`
      - 其他（包括虚空）：退出滑翔状态，触发`坠机`
  - `降落`：
    - 飞行阻力修正：在平地/水面上方一格会迅速降低竖向速率以允许贴地/水面滑行
    - 速率达到`0.6`进行滑翔（复飞）
  - `坠机`:
    - 对乘客造成与速度正相关的伤害，并触发`100`游戏刻的点燃
    - 乘客将被弹出矿车，矿车实体将被移除
    - 出现`ENTITY_GENERIC_EXPLODE`音效和`EXPLOSION`粒子效果
    - 尝试在落点周围创建持续时间为`1200`游戏刻的标准大小`温泉`
  - 矢量速率低于`0.2`的滑翔矿车将进入`失速`状态
    - 重新受到重力影响
    - 飞行阻力复原
    - 下降率过快可能导致重新进入滑翔状态而触发`坠机`
  - 下车触发`弹射`状态
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
- 矿车航行功能（实验性玩法）：
  - 仅对玩家正在乘坐的矿车生效
  - 在`降落`状态下触碰水面可进入`航行`状态
  - `航行`：特殊的滑翔/飞行状态
    - 具有全部的滑翔特性
    - 操纵方式同飞行
    - 航行状态下，极速只有纸面数据的一半
    - 可以完全没入水中
- 载具速度清零：可以通过`/vehicle stop`或其缩写`/ve s`将载具速度清零（滑翔/飞行不生效）
- 载具速度反向：可以通过`/vehicle back`或其缩写`/ve b`将载具速度反向（滑翔/飞行不生效）
### 温泉
- 温泉水可以提供
  - 抗性提升2`RESISTANCE Ⅱ`（蒸汽、水中生效）
  - 生命恢复2`REGENERATION Ⅱ`（蒸汽、水中生效）
  - 潮涌能量`CONDUIT_POWER`（仅水中）
  - 以及时间不定的反胃（模拟眩晕）`NAUSEA`效果（仅水中）
- 温泉水具有`CLOUD`、`WHITE_SMOKE`、`BUBBLE`粒子效果
- 玩家位于水中时可向水中丢弃火焰弹`FIRE_CHARGE`
  - 以火焰弹爆裂点为中心创建一片无限时间的标准大小温泉水域（OP权限生效）
  - 火焰弹爆裂时，出现`BLOCK_LAVA_EXTINGUISH`音效和`FLAME`粒子效果
- 玩家位于水中时可向水中丢弃雪球`SNOWBALL`
  - 以雪球爆裂点为中心移除一片标准大小温泉水域（OP权限生效）
  - 雪球爆裂时，出现`BLOCK_SNOW_STEP`音效和`SNOWFLAKE`粒子效果
- 矿车在水上`坠机`时，以坠机点为中心创建一片持续时间为`1200`游戏刻的标准大小温泉水域
- 由火焰弹生成的温泉会在开启持久化后保存温泉方块到指定的数据库，而矿车的不会
## 侵入物品
- 矿车 `MINECART` 实体
  - 玩家乘坐时
- 回溯指针`RECOVERY_COMPASS` 主手位物品
  - 占用 左键单击空气`LEFT_CLICK_AIR`
  - 占用 左键交互方块`LEFT_CLICK_BLOCK`
  - 占用 右键单击空气`RIGHT_CLICK_AIR`
- 火焰弹`FIRE_CHARGE` 掉落物
- 雪球`SNOWBALL` 掉落物
## 注意事项
- 速度超过一定值后（不小于0.4格每刻），转弯会脱轨，可进入滑翔状态但不推荐（脱轨时机和方向不定）
## 温泉持久化说明
- 使用`MySQL`作为数据库管理系统，`MyBatis`作为持久层框架
- 正确放置配置文件将开启温泉持久化功能，否则不会启用
### 数据库表结构
本表保存一个单独服务器的温泉方块，多服务器集群需要分别部署数据库表、配置文件和插件本体
```SQL
--字符集和比较依据可自选
CREATE TABLE `数据库表名`  (
  `id` bigint NOT NULL,
  `world` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `x` double NOT NULL,
  `y` double NOT NULL,
  `z` double NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
)
--主键不要选择自增，自增操作在后端完成
```
### MyBatis配置
- 配置文件`betterminecart-mybatis-config.xml`内容应该如下，中文内容需要修改为对应的参数
- 本文件并应与插件.jar包位于同一文件夹下
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