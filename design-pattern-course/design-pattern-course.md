# 设计模式课程笔记

## 七大设计原则
1. Single Responsibility Principle
2. Open-Closed Principle - 对扩展开放，对修改关闭
3. Liskov Substitution Principle - 任何基类可以出现的地方，子类一定可以出现
当添加新的子类时，只添加不覆写
4. Law of Demeter - 最少知道原则
5. Interface Segregation Principle
6. Dependence Inversion Principle - 高层代码不依赖于底层实现
7. Composite Reuse Principle - 合成复用原则，多用关联，少用继承

“Sol(l)id Core”

## 工厂模式

### 一、简单工厂模式
```java
/*** Product Interface ***/

public interface AutoInsurance {
	void getInsurInfo();
}

/*** Product Implementations ***/

public class BodyInjury implements AutoInsurance {
	@Override
	public void getInsurInfo() {
		System.out.println("get body injury insurance info..");
	}
}

public class Collision implements AutoInsurance {
	@Override
	public void getInsurInfo() {
		System.out.println("get collision insurance info..");
	}
}

/*** Simple Facotry ***/

public class PolicyProducer {
	public static AutoInsurance getPolicyObj(String option) {
		AutoInsurance ai = null;
		if (option.equals("BodyInjury")) {
			ai = new BodyInjury();
		} else {
			ai = new Collision();
		}
		return ai;
	}
}

/*** Client ***/

public class Client {
	public static void main(String[] args) {
		String event = "BodyInjury";

		AutoInsurance ai = PolicyProducer.getPolicyObj(event);
		ai.getInsurInfo();
	}
}

```

要添加新的对象类型，需要修改工厂类，不需要修改客户端。

弊端：不符合开闭原则

### 二、工厂方法模式
工厂类作为接口。

```java
/*** Factory ***/

public interface PolicyProducer {
	public AutoInsurance getPolicyObj();
}

/*** Factory Implementations ***/

public class BodyPolicy implements PolicyProducer {
	@Override
	public AutoInsurance getPolicyObj() {
		return new BodyInjury();
	}
}

public class CollisionPolicy implements PolicyProducer {
	@Override
	public AutoInsurance getPolicyObj() {
		return new Collision();
	}
}

/*** AutoInsurance and its implementations... (refer to Simple-Factory). ***/

/*** Client ***/

public class Client {
	public static void main(String[] args) {
		String event = "BodyInjury";

		PolicyProducer policyProducer = null;
		if (event.equals("BodyInjury")) {
			policyProducer = new BodyPolicy();
		} else {
			policyProducer = new Collision();
		}

		AutoInsurance ai = policyProducer.getPolicyObj();
		ai.getInsurInfo();
	}
}

```

要添加新的对象类型，不需要修改工厂类，只要创建新的工厂类的实现，因此它支持开闭原则；但是需要在客户端添加代码。

### 三、抽象工厂模式
当生产模式存在类似交叠的关系时，就有一个产品族+工厂族的问题，此时就应该使用抽象工厂模式。

为了比较好地理解抽象工厂，这里讲述两个比较不同的例子。

样例一的产品族为：高档别墅，中档别墅，高档公寓，中档公寓；
样例二的产品族为：颜色产品（包括蓝色，黄色等），图形产品（包括圆形，矩形等）。

首先提取出产品族中的属性。对于样例一，首先分析产品结构，可以提取出两个属性：
- 类型（type）
	- 别墅（house）
	- 公寓（condo）
- 级别（class）
	- 高档（super）
	- 中档（medium）

对于样例二，同样可以提取出属性：
- 颜色（color）
	- 蓝色（blue）
	- 黄色（yellow）
- 形状（shape）
	- 圆形（circle）
	- 矩形（rectangle）

但两个样例的区别在于，样例一的产品是属性嵌合的（一个产品必须同时具备两个属性），而样例二是属性独立的（要么是颜色产品，要么是形状产品）。因此，针对两种不同的产品模式，就有不同的设计思路。

为了方便，我们把前者叫做组合属性产品，后者叫做单一属性产品。

确定产品的属性间关系后，就可以开始为产品建模了。

针对组合属性产品，我们可以**选取其中一种属性作为产品的接口**。样例一的语境下显然应该选类型这个属性更合适。接下来就可以把产品族和工厂族划得更清晰一些：
- 产品族
	- 别墅建筑（House building）
	- 公寓建筑（Condo building）
- 工厂族
	- 高档建筑工厂（Super class factory）
	- 中档建筑工厂（Medium class factory）

相应的，对于属于单一属性产品族的样例二则应该是：
- 产品族
	- Rectangle \<Shape\>
	- Circle \<Shape\>
	- Blue \<Color\>
	- Yellow \<Color\>
- 工厂族
	- 图形工厂（ShapeFactory）
	- 颜色工厂（ColorFactory）

从上面可以看出，两者的产品类实现过程也是不一样的。样例一有两个接口类（`House` `Condo`），两个实现类；样例二有两个接口类，四个实现类。

对于组合属性产品，它的两个属性可以在逻辑上分离。在前面的步骤中，样例一只是以建筑类型这个属性来定义接口，另一个属性——建筑等级——被放到工厂层去了，也就是第二属性的数量决定工厂实现类的数量。这样把产品属性在处理逻辑上分离的做法是明显不适用于单一属性产品的。

两者在抽象工厂类的定义上也是有差异的。乍看上去都是有两个返回产品对象的方法，但样例一返回的是 `House` 和 `Condo` 对象，样例二返回的是 `Shape` 和 `Color` 对象，发现差异了吗？
![][1]


---

有些抽象工厂的实现中，还有一层创建具体工厂的逻辑（工厂生成器），在上面这个例子中通过为抽象工厂添加静态函数 `getBuildingFactory` 省去了。

如果要在原有的产品族（`House` + `Condo`）中增加新的产品，则在工厂阶层只需要增加工厂实类，这种情况下符合开闭原则；如果要扩展产品族，则必须在每个工厂实类中增加方法（getXXX），这种情况下不符合开闭原则。

实现代码：
- 样例一：[code][2]
- 样例二：参考[Tutorialspoint][3] 或菜鸟教程

[1]:	/design-pattern-course/Xnip2018-10-08_14-59-10.jpg
[2]:	/design-pattern-course/example-I.java
[3]:	https://www.tutorialspoint.com/design_pattern/abstract_factory_pattern.htm