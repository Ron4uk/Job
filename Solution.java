package forGit.Java;

/**
 * Created by Oleg on 03.08.2019.
 */




public class Solution {

    public static void main(String[] args)  {
        int[] array = {13, 3, 8, 1, 15, 2, 3, 7, 4};
        System.out.print("Массив до сортировки: ");
        for (int i = 0; i < array.length; i++)
            System.out.print(array[i] + " ");
        System.out.println("");
        array = sortByIndex(array, 0, array.length);   //указываем индексы, с какого по какой необходимо сделать сортировку
        System.out.print("Массив после сортировки: ");
        for (int i = 0; i < array.length; i++)
            System.out.print(array[i] + " ");
    }

    public static int[] sortByIndex(int[] array, int start, int end) {
        int[] newArray;
        int count =start;
        if(start<end&& end<=array.length) {
            newArray = new int[end-start];
            for(int i=0; i<newArray.length;i++){
                newArray[i]=array[count];
                count++;
            }
            count = start;
            newArray=sort(newArray);
         for(int i=0; i<newArray.length; i++){
            array[count]=newArray[i];
            count++;
            }
            return array;
        }
        else {System.out.println("Incorrect index for sort");
        return array;
        }

    }

    public static int[] sort(int[] array)  { // сортировка Массива который передается в функцию


            if (array == null) {
                return null;
            }

            if (array.length < 2) {
                return array;
            }
            // копируем левую часть от начала до середины
            int[] arrayB = new int[array.length / 2];
            System.arraycopy(array, 0, arrayB, 0, array.length / 2);

            // копируем правую часть от середины до конца массива, вычитаем из длины первую часть
            int[] arrayC = new int[array.length - array.length / 2];
            System.arraycopy(array, array.length / 2, arrayC, 0, array.length - array.length / 2);


            arrayB = sort(arrayB); //   через рекурсию добиваемся 1 элемента в массиве, а даллее раскричаемся в обратную сторону
            arrayC = sort(arrayC); //   и через mergeArray объединяем получившиеся массивы


            return mergeArray(arrayB, arrayC);


    }

    public static int[] mergeArray(int[] arrayА, int[] arrayB) {

        int[] arrayC = new int[arrayА.length + arrayB.length];
        int positionA = 0, positionB = 0;

        for (int i = 0; i < arrayC.length; i++) {
            if (positionA == arrayА.length) {
                arrayC[i] = arrayB[positionB];
                positionB++;
            } else if (positionB == arrayB.length) {
                arrayC[i] = arrayА[positionA];
                positionA++;
            } else if (arrayА[positionA] < arrayB[positionB]) {
                arrayC[i] = arrayА[positionA];
                positionA++;
            } else {
                arrayC[i] = arrayB[positionB];
                positionB++;
            }
        }
        return arrayC;
    }

    }
