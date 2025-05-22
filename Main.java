package scholar;

import java.util.ArrayList;

public class Main {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        new login(); // Start with login page
    }

    public static boolean isAdmin(String username, String password) {
        return username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD);
    }

    public static ArrayList<Scholarship> mergeSort(ArrayList<Scholarship> list) {
        if (list.size() <= 1) return list;

        int mid = list.size() / 2;
        ArrayList<Scholarship> left = new ArrayList<>(list.subList(0, mid));
        ArrayList<Scholarship> right = new ArrayList<>(list.subList(mid, list.size()));

        return merge(mergeSort(left), mergeSort(right));
    }

    private static ArrayList<Scholarship> merge(ArrayList<Scholarship> left, ArrayList<Scholarship> right) {
        ArrayList<Scholarship> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i).getTitle().compareTo(right.get(j).getTitle()) < 0) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }

        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));

        return result;
    }
}

