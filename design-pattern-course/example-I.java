/*** Product Group ***/

public interface House {
    public String getHouseInfo();
}

public interface Condo {
    public String getCondoInfo();
}

/*** Product Implementations ***/

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