class Dog {
    String breed;
    String size;
    int age;
    String color;

    public String getInfo() {
        return ("Breed is: " + breed + " Size is: " + size + " Age is: " + age + " Color is: " + color);
    }
}
public class lec_1 {
    public static void main(String[] args) {
        Dog Neapolitan_Mastiff = new Dog();
        Neapolitan_Mastiff.breed="Neapolitan_Mastiff";
        Neapolitan_Mastiff.size="Large";
        Neapolitan_Mastiff.age=5;
        Neapolitan_Mastiff.color="Black";
        System.out.println(Neapolitan_Mastiff.getInfo());

        Dog Maltese = new Dog();
        Maltese.breed="Maltese";
        Maltese.size="Small";
        Maltese.age=2;
        Maltese.color="White";
        System.out.println(Maltese.getInfo());

        Dog Chow_Chow = new Dog();
        Chow_Chow.breed="Chow Chow";
        Chow_Chow.size="Medium";
        Chow_Chow.age=3;
        Chow_Chow.color="Brown";
        System.out.println(Chow_Chow.getInfo());
    }
}