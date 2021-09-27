package algoimage;

public class ComplexNumber {
	
	private double real;
	private double imaginary;
	
	public static ComplexNumber of(double real, double imaginary) {
		return new ComplexNumber(real, imaginary);
	}
	
	private ComplexNumber(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}

	public double abs() {
        return Math.hypot(real, imaginary);
    }
    
    public ComplexNumber plus(ComplexNumber b) {
        double real = this.real + b.real;
        double imag = this.imaginary + b.imaginary;
        return new ComplexNumber(real, imag);
    }
    
    public ComplexNumber times(ComplexNumber b) {
        double real = this.real * b.real - this.imaginary * b.imaginary;
        double imag = this.real * b.imaginary + this.imaginary * b.real;
        return new ComplexNumber(real, imag);
    }
    
    public int iterate(ComplexNumber z, int maximumIterations) {
        for (int t = 0; t < maximumIterations; t++) {
            if (z.abs() > 2.0) return t;
            z = z.times(z).plus(this);
        }
        return maximumIterations - 1;
    }
}
