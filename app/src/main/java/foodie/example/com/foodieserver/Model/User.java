package foodie.example.com.foodieserver.Model;

public class User {
    private String Name, Password, Phone, IsStaff, restaurantId,SecureCode;
    private double rewardCash;

    public User(String name, String password) {
        Name = name;
        Password = password;
    }

    public User(String name, String password, String phone, String secureCode, String isStaff, String restaurantId, double rewardCash) {
        Name = name;
        Password = password;
        Phone = phone;
        SecureCode = secureCode;
        IsStaff = isStaff;
        this.restaurantId = restaurantId;
        this.rewardCash = rewardCash;
    }

    public User() {
    }

    public String getSecureCode() {
        return SecureCode;
    }

    public void setSecureCode(String secureCode) {
        SecureCode = secureCode;
    }

    public double getRewardCash() {
        return rewardCash;
    }

    public void setRewardCash(double rewardCash) {
        this.rewardCash = rewardCash;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

}
