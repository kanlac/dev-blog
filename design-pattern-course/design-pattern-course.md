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

## 设计模式分类概览
- 创建型设计模式
	- 工厂模式（Factory Pattern）
	- 单例模式（Singleton Pattern）
	- 生成器模式（Builder Pattern）
- 结构型设计模式
- 行为型设计模式

## 工厂模式（Factory Pattern）

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

工厂方法模式是三个工厂模式中对开闭原则支持最好的。要添加新的对象类型，不需要修改工厂类，只要创建新的工厂类的实现，因此它支持开闭原则；但是需要在客户端添加代码。

### 三、抽象工厂模式
当生产模式存在类似交叠的关系时，就有一个产品族+工厂族的问题，此时就应该使用抽象工厂模式。

为了比较好地理解抽象工厂，这里讲述两个例子，它们所用到的也是不同的思路。

样例一的产品族为：高档别墅，中档别墅，高档公寓，中档公寓；
样例二的产品族为：颜色产品（包括蓝色，黄色等），图形产品（包括圆形，矩形等）。

做后面的分析时，请结合具体的实现代码理解：样例一[代码][1]；样例二参考 [Tutorialspoint][2] 或菜鸟教程。

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

为了方便，我们把前者叫做**组合属性**产品，后者叫做**单一属性**产品。

确定产品的属性间关系后，就可以开始为产品建模了。

针对组合属性产品，我们可以更进一步，把样例一中的\<类型\>看作**类别属性**，\<级别\>看作**修饰属性**，之所以要这样做，是为了实现两者在逻辑上的分离。**定义产品接口时将基于类别属性，而工厂层将基于修饰属性**。

再举一个组合属性产品的例子：男（女）鞋、男（女）上衣、男（女）下装。哪个应该是类别属性？明显的，\<男\>\<女\>本身不能是产品，\<鞋\>\<上衣\>\<下装\>才是，因此前者是修饰属性，后者是类别属性。

接下来就可以得出产品族和工厂族分别为：
- 产品族
	- 别墅建筑（House）
	- 公寓建筑（Condo）
- 工厂族
	- 高档建筑工厂（superBuildingFactory）
	- 中档建筑工厂（mediumBuildingFactory）

相对应的，对于属于单一属性产品的样例二则应该是：
- 产品族
	- Rectangle \<Shape\\\\\>
	- Circle \<Shape\\\\\>
	- Blue \<Color\\\\\>
	- Yellow \<Color\\\\\>
- 工厂族
	- 图形工厂（ShapeFactory）
	- 颜色工厂（ColorFactory）

从上面可以看出，两者的产品类实现过程也是不一样的。同样是四个产品，样例一有两个接口类（`House` `Condo`），两个实现类；样例二有两个接口类，四个实现类。

对于组合属性产品，它的两个属性可以在逻辑上分离。在前面的步骤中，样例一只是以\<建筑类型\>这个属性来定义接口，另一个属性——\<建筑等级\>——被放到工厂层去了，也就是修饰属性决定工厂层实现。

两者在抽象工厂类的定义上也是有差异的。乍看上去都是有两个返回产品对象的方法，但样例一返回的是 `House` 和 `Condo` 对象，样例二返回的是 `Shape` 和 `Color` 对象。
![发现差异了吗？][image-1]

最后是抽象工厂的实现。对于组合属性产品，工厂实现类基于修饰属性下的取值多少（两种房屋等级）；对于单一属性产品，工厂实现类基于产品族的属性本身（颜色和形状）。

有些抽象工厂的实现中，还有一层创建具体工厂的逻辑（`FactoryProducer`），在样例一的代码中通过为抽象工厂添加静态函数 `getBuildingFactory()` 省去了。

#### 抽象工厂与开闭原则
抽象工厂产品族的扩展可以分为两种，一种是**产品属性本身的扩展**，如在 Shape 和 Color 外增加一个 Opacity，另一种是**属性下的值的扩展**，如在\<\<`House`\>\>和\<\<`Condo`\>\>外增加一种房屋类型。

![][image-2]

> 《软件设计模式与体系结构》中考虑扩展性时，采取的是「工厂层」和「产品层」的视角去分析，但考虑到这里组合属性和单一属性不同样例的区别，我不采用书中的分析方法。

##### 分析组合属性产品族的扩展性
对于**产品属性本身的扩展**，假设我们在样例一的\<房屋类型\>、\<房屋等级\>之外，再添加一个组合属性\<房屋位置\>（取值有\<\<`Urban`\>\>和\<\<`Suburb`\>\>）。由于组合属性样例采用了把原有两类属性在逻辑上分离的方式，所以现在新加入的第三组属性，要么和类别属性（\<房屋类型\>）一起放到产品层，要么和修饰属性（\<房屋等级\>）一起放入工厂层。实际上，稍微思考一些就发现，两种做法都是不贴合实际的。因此，这种情况下是**不符合开闭原则**的。

对于**属性下的值的扩展**，假设在\<房屋等级\>里添加一个取值\<\<`Basic`\>\>，那么我们要做的：a) 添加产品层实现 `BasicHouse` `BasicCondo`；b) 添加工厂层实现 `BasicBuildingFactory`。可见，这种情况下是**符合开闭原则**的。

##### 分析单一属性产品族的扩展性
对于**产品属性本身的扩展**，假设在样例二的\<颜色\>\<形状\>之外，再添加一个单一属性\<透明度\>（取值有\<\<`translucence `\>\>\<\<`opacite `\>\>）。则我们必须要在抽象工厂中添加方法 `getOpacity`，因此这种情况下是**不符合开闭原则**的。

对于**属性下的值的扩展**，假设在\<颜色\>之下添加一个取值\<\<`Red`\>\>，那么我们只需添加一个 `Color` 的实现类 `Red`，只要工厂类实现使用了 Java 映射，就不需要做客户端以外的更改了。因此，这种情况下是**符合开闭原则**的。

由上，我们证明了**不论产品族是单一属性还是组合属性，抽象工厂都是对产品属性本身的扩展关闭，对属性下的值的扩展开放的**。

## 单例模式（Singleton Pattern）
可以分为三类：
1. 构造器私有，每次从外部获取实例时重新实例化；
2. 构造器私有，仅在第一次初始化；
3. 构造器公有，以 HashMap 存储实例，实现可继承性。

前两种较常用，它们都不允许在外部实例化，因而也不能被继承。唯一实例对象的初始化和获取函数（`getInstance()`）都是静态的。

## 生成器模式（Builder Pattern）
> 使用场景样例：一个文本编辑器应用的导出模块，导出类型有 txt，MarkDown，Word，HTML 等。

生成器模式的主要思想是抽象出创建对象的步骤（文本标题、正文、元数据），使得这些步骤的不同实现可以创建对象的不同表示（HTML、MarkDown 等格式）。生成器模式主要包含以下几部分：
- _Builder_：创建并组装产品零件，提供一个方法返回产品对象
- _Director_：使用 Builder 提供的接口，提供产品参数，并能控制组装顺序而得到完整产品对象
- _Product_：产品对象模型

生成器模式的**优点**在于它解藕了产品的封装过程（Director）和部件表示/装配细节（Builder），改善了程序的模块化，可以方便地添加生成器或更改产品的内部表达形式。

生成器模式与工厂模式类似的地方在于，两者都是把对象的创建交给一个单独的类去完成，主要区别是，工厂模式返回一个类（族），而生成器模式**逐步按照次序构建**一个复杂的对象，最后该对象被返回。

[1]:	/design-pattern-course/example-I.java
[2]:	https://www.tutorialspoint.com/design_pattern/abstract_factory_pattern.htm

[image-1]:	/design-pattern-course/Desktop.png "screenshot"
[image-2]:	/design-pattern-course/Desktop2.png