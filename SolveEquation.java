import javax.swing.JOptionPane;
public class SolveEquation {
    public static void main(String[] args) {
        String strNotification;
        strNotification = "";
        String[] options = { "Linear Equation", "Linear System" };
        var choice = JOptionPane.showOptionDialog(null, "I'm Le Tran Long, please select one kind of equation", "Long", 0, 2, null, options, options[0]);

        if (choice == 0){
            String lequation = JOptionPane.showInputDialog(null, "Please input the coefficients of the linear equation" + "\nSeparated by space", "Input coefficients",JOptionPane.INFORMATION_MESSAGE);
            String[] lequationCoefficients = lequation.split(" ");
            Double a = Double.parseDouble(lequationCoefficients[0]);
            Double b = Double.parseDouble(lequationCoefficients[1]);
            if (a == 0)
                {
                    if (b == 0) {
                        strNotification += "The equation has infinitely many solutions."; 
                   }
                   else {
                        strNotification += "No solution";
                   }
                }
            else {
                strNotification += "The solution is " + -b/a;
            }
        }
        else {
            String equation1 = JOptionPane.showInputDialog(null, "Please input the coefficients of the first equation" + "\nSeparated by space", "Input coefficients", JOptionPane.INFORMATION_MESSAGE);
            String equation2 = JOptionPane.showInputDialog(null, "Then input the coefficients of the second equation" + "\nSeparated by space", "Input coefficients", JOptionPane.INFORMATION_MESSAGE);
            String[] equation1Coefficients = equation1.split(" ");
            String[] equation2Coefficients = equation2.split(" ");
            double a11 = Double.parseDouble(equation1Coefficients[0]);
            double a12 = Double.parseDouble(equation1Coefficients[1]);
            double b1 = Double.parseDouble(equation1Coefficients[2]);
            double a21 = Double.parseDouble(equation2Coefficients[0]);
            double a22 = Double.parseDouble(equation2Coefficients[1]);
            double b2 = Double.parseDouble(equation2Coefficients[2]);
            double determinant = a11 * a22 - a21 * a12;
            if (determinant == 0) {
                strNotification += "The system of equations has no unique solution";
                
            }
            else {
                double x = (b1 * a22 - b2 * a12) / determinant;
                double y = (a11 * b2 - a21 * b1) / determinant;
                strNotification += "The solution of the system is \n" 
                                            + "x = " + x 
                                            + "\ny = " + y;
            }

        }   
        JOptionPane.showMessageDialog(null, strNotification, "Result", 1);
        System.exit(0);
    }
}

