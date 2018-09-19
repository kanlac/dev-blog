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

要添加新的对象类型，不需要修改工厂类，只要创建新的工厂类的应用，因此它支持开闭原则；但是需要在客户端添加代码。

### 三、抽象工厂模式
当生产模式存在类似这样交叠的关系时：
- 高档别墅
- 中档别墅
- 高档公寓
- 中档公寓
或者：
- 海尔
	- 电冰箱
	- 电视机
- 三星
	- 电冰箱
	- 电视机
就有一个产品族+工厂族的问题，此时就应该使用抽象工厂模式。

```java
/*** Products ***/

public interface House {
	public String getHouseInfo();
}

public interface Condo {
	public String getCondoInfo();
}

/*** Products Implementations ***/

public class MediumHouse implements House {
	@Override
	public String getHouseInfo() {
		return "It's a medium house.";
	}
}

public class SuperHouse implements House {
	@Override
	public String getHouseInfo() {
		return "It's a super house.";
	}
}

public class MediumCondo implements Condo {
	@Override
	public String getCondoInfo() {
		return "It's a medium condo.";
	}
}

public class SuperCondo implements Condo {
	@Override
	public String getCondoInfo() {
		return "It's a super condo.";
	}
}

/*** Abstract Factory ***/

public abstract class BuildingFactory {

	public abstract House getHouse();
	public abstract Condo getCondo();

	public static BuildingFactory getBuildingFactory(String buildingClass) {
		BuildingFactory factory = null;

		if (buildingClass.equals(Constants.BuildingClass.MEDIUM)) {
			factory = new MediumBuildingFactory();
		} else if (buildingClass.equals(Constants.BuildingClass.SUPER)) {
			factory = new SuperBuildingFactory();
		}

		return factory;
	}
}

/*** Facotry Implementations ***/

public class MediumBuildingFactory extends BuildingFactory {
	@Override
	public House getHouse() {
		return new MediumHouse();
	}
	@Override
	public Condo getCondo() {
		return new MediumCondo();
	}
}

public class SuperBuildingFactory extends BuildingFactory {
	@Override
	public House getHouse() {
		return new SuperHouse();
	}
	@Override
	public Condo getCondo() {
		return new SuperCondo();
	}
}

/*** Client ***/

public class Cilent {
	public static void main(String[] args) {
		// arguments
		String buildingClass = "super class";
		String buildingType = "condo";

        // initialize factory by building class
		BuildingFactory factory = BuildingFactory.getBuildingFactory(buildingClass);

        // get building object by type
		if (buildingType.equals(Constants.BuildingType.HOUSE)) {
			House house = factory.getHouse();
			System.out.println(house.getHouseInfo());
		} else if (buildingType.equals(Constants.BuildingType.CONDO)) {
			Condo condo = factory.getCondo();
			System.out.println(condo.getCondoInfo());
		}
	}
}

/*** Constants ***/

public final class Constants {

	public final class BuildingClass {
		public static final String SUPER = "super class";
		public static final String MEDIUM = "medium class";
	}
	public final class BuildingType {
		public static final String HOUSE = "house";
		public static final String CONDO = "condo";
	}

}
```
