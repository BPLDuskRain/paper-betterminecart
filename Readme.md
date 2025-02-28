# Better Minecart
Author: DuskRainFall
## 插件效果
### 载具
- 显示载具速度：每隔`40`个载具移动事件之后，计算一次载具移动速度
- 控制矿车极速：通过手持物品左右键乘坐矿车极速，速度区间`0.05~1.5`格每刻
- 加强矿车强度：会对实体造成强力击飞，并免疫撞击
- 矿车滑翔功能：速度超过一定值的腾空矿车进行滑翔
- 矿车飞行功能：滑翔中的矿车可以通过手持物品驾驶
- 载具速度清零：可以通过`/vehicle stop`或其缩写`/ve s`将载具速度清零
- 载具速度反向：可以通过`/vehicle back`或其缩写`/ve b`将载具速度反向
### 温泉（OP权限）
- 温泉水可以提供
  - 潮涌能量`CONDUIT_POWER`
  - 抗性提升`RESISTANCE`
  - 生命恢复`REGENERATION`
  - 以及一定程度的反胃（眩晕）`NAUSEA`效果
- 温泉水具有`CLOUD`、`WHITE_SMOKE`、`BUBBLE`粒子效果
## 侵入物品
- 回溯指针`RECOVERY_COMPASS`：
  - 在轮轨/飞行状态下用于调整矿车极速（主手有效）
    - 左击空气/方块极速翻倍
    - 右击空气极速减半
  - 在飞行状态下用于控制矿车方向（主手有效）
    - 矿车将向视角方向逐渐增加速度
- 火焰弹`FIRE_CHARGE`：玩家位于水中时丢弃可用于创建一片温泉水域
- 雪球`SNOWBALL`：玩家位于水中时丢弃可用于移除一片温泉水域
## 其他效果
- 在降低极速时，出现`BLOCK_ANVIL_HIT`音效和`LAVA`粒子效果
- 在提升极速时，出现`ENTITY_FIREWORK_ROCKET_LAUNCH`音效和`FIREWORK`粒子效果
- 在火焰弹入水时，出现`BLOCK_LAVA_EXTINGUISH`音效和`FLAME`粒子效果
- 在雪球入水时，出现`BLOCK_SNOW_STEP`音效和`SNOWFLAKE`粒子效果
## 注意事项
- 速度超过一定值后（不小于0.4格每刻），转弯会脱轨