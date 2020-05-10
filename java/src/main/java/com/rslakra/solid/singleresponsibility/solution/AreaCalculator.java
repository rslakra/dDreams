package com.rslakra.solid.singleresponsibility.solution;

import com.rslakra.solid.singleresponsibility.problem.Circle;
import com.rslakra.solid.singleresponsibility.problem.Square;

/**
 * Author: Rohtash Singh Lakra
 * Created: 5/9/20 1:13 PM
 * Version: 1.0.0
 */
public class AreaCalculator {
    private Object[] objects;

    public AreaCalculator(Object... objects) {
        this.objects = objects;
    }

    public double sum() {
        double sum = 0;
        for (Object shape : objects) {
            if (shape instanceof Circle) {
                double radius = ((Circle) shape).getRadius();
                sum += (3.14 * Math.pow(radius, 2));
            } else if (shape instanceof Square) {
                double length = ((Square) shape).getLength();
                sum += Math.pow(length, 2);
            }
        }

        return sum;
    }


    public static void main(String[] args) {
        AreaCalculator areaCalculator = new AreaCalculator(new Circle(3), new Square(4));
        Renderer renderer = new Renderer(areaCalculator.sum());
        renderer.renderConsole();
        renderer.renderJson();
        renderer.renderHtml();
    }
}
