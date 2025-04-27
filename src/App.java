import javax.swing.JOptionPane;
import java.text.DecimalFormat;

public class App {
    static int indColEntra, indFilaSal, contMultiple=0;
    static double z=0, pivote;
        
    public static void main(String[] args){
        String funcion;
        int m, d, n, a=0;
        m = Integer.parseInt(JOptionPane.showInputDialog("Ingrese la Cantidad de Restricciones: "));
        d = Integer.parseInt(JOptionPane.showInputDialog("Ingrese la Cantidad de Variables de decisión: "));
        
        funcion = JOptionPane.showInputDialog("Ingrese el objetivo de la Función (maximizar o minimizar): ");
        switch (funcion){
            case "maximizar" -> a = 0;
            case "minimizar" -> a = m;
        }
        
        n = (d+m+a); //"n" = "d" (#var. decision) + "m" (#var. holgura) + "a" (#var. artificial)
        
        double cn[] = new double[n]; //Coeficientes de var. de Función Objetivo
        double amn[][] = new double[m][n]; //Coeficientes de cada variable por restricción
        double bm[] = new double[m]; //Restricción
        double bk[] = new double[m];
        double ck[] = new double[m];
        int xk[] = new int[m];
        double zn[] = new double[n];
        double zn_cn[] = new double[n];
        
        ingresoCn(cn,n,d,a);
        ingresoAmn_Bm(bm,amn,m,n,d,a);
        vectoresK(bk,ck,xk,m,bm,cn,d,a);
        calculoZn(zn,bk,ck,amn,m,n);
        calculoZn_Cn(zn_cn,zn,cn,m,n);
        mostrarTabla(funcion,cn,amn,bk,ck,xk,zn,zn_cn,m,n,d,a);
        condicionOptima(funcion,cn,amn,bk,ck,xk,zn,zn_cn,m,n,d,a); //Analizando si se llegó a la Solución Optima
    }
    public static void ingresoCn(double cn[],int n,int d,int a){ //Z = Cn*Xn //Función Objetivo
        for (int j = 0; j < n; j++){
            if (a==0){ //maximizar
                if (j < d){
                    cn[j] = Double.parseDouble(JOptionPane.showInputDialog(
                            "PARA: Z = Cn*Xn"+
                            "\nIngrese el valor de C"+(j+1)+": "));                
                }
                else if (j < n){
                    cn[j] = 0;
                }
            }
            else if (a!=0){ //minimizar
                if (j < d){
                    cn[j] = Double.parseDouble(JOptionPane.showInputDialog(
                            "PARA: Z = Cn*Xn"+
                            "\nIngrese el valor de C"+(j+1)+": "));                
                }
                else if (j < (d+a)){
                    cn[j] = 0;
                }
                else if (j < n){
                    cn[j] = 100000;
                }
            }
        }
    }
    public static void ingresoAmn_Bm(double bm[],double amn[][],int m,int n,int d,int a){ //Amn*Xn <=> Bm //Restricciones
        for (int i = 0; i < m; i++){
            for (int j = 0; j < n; j++){
                if (a==0){ //maximizar
                    if (j < d){
                        amn[i][j] = Double.parseDouble(JOptionPane.showInputDialog(
                                    "PARA: Amn*Xn = Bm"+
                                    "\nEN LA RESTRICCION m="+(i+1)+
                                    "\nIngrese el valor de A"+(i+1)+(j+1)+" de X"+(j+1)+": "));               
                    }
                    else if (j < n){
                        if (j==(i+d))
                            amn[i][j] = 1;
                        else
                            amn[i][j] = 0;
                    }
                }
                else if (a!=0){ //minimizar
                    if (j < d){
                        amn[i][j] = Double.parseDouble(JOptionPane.showInputDialog(
                                    "PARA: Amn*Xn = Bm"+
                                    "\nEN LA RESTRICCION m="+(i+1)+
                                    "\nIngrese el valor de A"+(i+1)+(j+1)+" de X"+(j+1)+": "));              
                    }
                    else if (j < (d+a)){
                        if (j==(i+d))
                            amn[i][j] = -1;
                        else
                            amn[i][j] = 0;
                    }
                    else if (j < n){
                        if (j==(i+d+a))
                            amn[i][j] = 1;
                        else
                            amn[i][j] = 0;
                    }
                }
            }
            bm[i] = Double.parseDouble(JOptionPane.showInputDialog(
                    "PARA: Amn*Xn = Bm"+
                    "\nEN LA RESTRICCION m="+(i+1)+
                    "\nIngrese el valor de B"+(i+1)+": "));
        }
    }
    public static void vectoresK(double bk[],double ck[],int xk[],int m,double bm[],double cn[],int d,int a){
        for (int k = 0; k < m; k++){
            bk[k] = bm[k];
            ck[k] = cn[k+d+a];
            xk[k] = (k+d+a+1);
        }
    }
    public static void calculoZn(double zn[],double bk[],double ck[],double amn[][],int m,int n){
        double tempZ,tempZn;
        for (int i = 0; i < m; i++){
            tempZ = ck[i]*bk[i];
            z += tempZ;
        }
        for (int j = 0; j < n; j++){
            for (int i = 0; i < m; i++){
                tempZn = ck[i]*amn[i][j];
                zn[j] += tempZn;
            }
        }
    }
    public static void calculoZn_Cn(double zn_cn[],double zn[],double cn[],int m,int n){        
        for (int j = 0; j < n; j++){
            zn_cn[j] = zn[j]-cn[j];
            if (zn_cn[j] == 0){
                contMultiple++; //contando para SOLUCIÓN MULTIPLE
            }
        }
    }
    public static void mostrarTabla(String funcion,double cn[],double amn[][],double bk[],double ck[],int xk[],double zn[],double zn_cn[],int m,int n,int d,int a){
        DecimalFormat obj=new DecimalFormat("####.##");
        System.out.print("\nTabla "+funcion+":\n\t\tCn");
        for (int j = 0; j < n; j++){
            System.out.print("\t"+obj.format(cn[j]));
        }
        System.out.println();
        
        System.out.print("Ck\tXk\tBk");
        for (int j = 0; j < n; j++){
            System.out.print("\tX"+(j+1));
        }
        System.out.println();
        
        for (int i = 0; i < m; i++){
            System.out.print(obj.format(ck[i])+"\tX"+xk[i]+"\t"+obj.format(bk[i]));
            for (int j = 0; j < n; j++){
                System.out.print("\t"+obj.format(amn[i][j]));
            }
            System.out.println();
        }
        
        System.out.print("Zn\t\t"+obj.format(z));
        for (int j = 0; j < n; j++){
            System.out.print("\t"+obj.format(zn[j]));
        }
        System.out.println();
        
        System.out.print("Zn-Cn\t\t");
        for (int j = 0; j < n; j++){
            System.out.print("\t"+obj.format(zn_cn[j]));
        }
        
        if (contMultiple > m){ //Verificando si es una SOLUCIÓN MULTIPLE
            System.out.print("\n*** ESTA TABLA ES UNA SOLUCION MULTIPLE ***");
        }
        contMultiple=0;
        System.out.println("\n");
    }
    public static void condicionOptima(String funcion,double cn[],double amn[][],double bk[],double ck[],int xk[],double zn[],double zn_cn[],int m,int n,int d,int a){
        DecimalFormat obj=new DecimalFormat("####.##");
        if (a != 0)
            System.exit(0); //Fin del programa para minimizar
        int contOptima=0;
        for (int j = 0; j < n; j++){
            if (zn_cn[j] >= 0){ //maximizar
                contOptima++; 
            }
        }
        if (contOptima == n){ //verifica que TODOS los zc-cn cumplan con ser 0 o positivos
            System.out.println("***** ESTA TABLA CONTIENE LOS DATOS DE LA SOLUCION OPTIMA *****");
            System.out.println("\nSOLUCION OPTIMA:\nZ = "+obj.format(z));
            for (int j = 0; j < d; j++){
                for (int i = 0; i < m; i++){
                    if (ck[i] == cn[j]) {
                        System.out.println("X"+xk[i]+" = "+obj.format(bk[i]));
                    }
                }
            }
            System.exit(0); //Fin del programa
        }
        else{
            construirTabla(funcion,cn,amn,bk,ck,xk,zn,zn_cn,m,n,d,a);
        }
    }
    public static void construirTabla(String funcion,double cn[],double amn[][],double bk[],double ck[],int xk[],double zn[],double zn_cn[],int m,int n,int d,int a){
        columnaEntrante(zn_cn,n);
        filaSaliente(bk,amn,m);
        datosFilaPivote(ck,cn,xk,bk,amn,n);
        datosFilasNoPivote(bk,amn,m,n);
        reiniciarValoresZn(zn,n);
        calculoZn(zn,bk,ck,amn,m,n);
        calculoZn_Cn(zn_cn,zn,cn,m,n);
        mostrarTabla(funcion,cn,amn,bk,ck,xk,zn,zn_cn,m,n,d,a);
        condicionOptima(funcion,cn,amn,bk,ck,xk,zn,zn_cn,m,n,d,a);
    }    
    public static void columnaEntrante(double zn_cn[],int n){
        double Zn_cn_Menor=0;
        for (int j = 0; j < n; j++){
            if (zn_cn[j] < 0){ //maximizar
                if (Zn_cn_Menor < Math.abs(zn_cn[j])){ //SI HUBIERA DOS COEFICIENTES IGUALES, toma el primero
                    Zn_cn_Menor = Math.abs(zn_cn[j]); //coeficiente mas negativo
                    indColEntra = j; //identifica el indice de la columna del coeficiente mas negativo
                }
            }
        }
        System.out.println("* COLUMNA ENTRANTE: "+(indColEntra+1));
    }
    public static void filaSaliente(double bk[],double amn[][],int m){
        double fila_Menor=999999;
        int contDegenerado=0;
        int contAcotado=0;
        for (int i = 0; i < m; i++){
            if (amn[i][indColEntra] > 0){
                if (fila_Menor > (bk[i]/amn[i][indColEntra]) && (bk[i]/amn[i][indColEntra]) > 0){ //SI HUBIERA DOS COEFICIENTES IGUALES, toma el primero
                    fila_Menor = bk[i]/amn[i][indColEntra];
                    indFilaSal = i;
                    pivote = amn[i][indColEntra];
                }
            }
            if ((bk[i]/amn[i][indColEntra]) <= 0){
                contAcotado++; //contando para FUNCIÓN NO ACOTADA
            }
        }
        if (contAcotado == m){ //Verificando si es una FUNCIÓN NO ACOTADA
            System.out.println("\n¡¡¡ FUNCION NO ACOTADA... ES IMPOSIBLE DE CONTINUAR !!!");
            System.exit(0); //Deteniendo el programa
        }
        for (int i = 0; i < m; i++){ //Contando para SOLUCION DEGENERADA (cuantos cocientes son iguales y a la vez son los menores positivos)
            if (fila_Menor == (bk[i]/amn[i][indColEntra]) && (bk[i]/amn[i][indColEntra]) > 0){
                contDegenerado++;
            }
        }
        if (contDegenerado > 1){ //Verificando si es una SOLUCIÓN DEGENERADA (debe haber más de 1 cociente menor positivo)
            System.out.println("***** ESTE ES UN CASO DE DEGENERACION *****");
        }
        System.out.println("* FILA SALIENTE: "+(indFilaSal+1));
    }
    public static void datosFilaPivote(double ck[],double cn[],int xk[],double bk[],double amn[][],int n){
        ck[indFilaSal] = cn[indColEntra];
        xk[indFilaSal] = (indColEntra+1);
        bk[indFilaSal] = bk[indFilaSal]/pivote;
        for (int j = 0; j < n; j++){
            amn[indFilaSal][j] = (amn[indFilaSal][j])/pivote;
        }
    }
    public static void datosFilasNoPivote(double bk[],double amn[][],int m,int n){
        double semipivote_temp;
        for (int i = 0; i < m; i++){
            if (i != indFilaSal){
                bk[i] = bk[i]-(amn[i][indColEntra]*bk[indFilaSal]);
            }
            semipivote_temp = amn[i][indColEntra];
            for (int j = 0; j < n; j++){
                if (i != indFilaSal){
                    amn[i][j] = amn[i][j]-(semipivote_temp*amn[indFilaSal][j]); //Fila nueva = Fila anterior - (coeficiente de fila anterior en columna de variable entrante*Fila entrante)
                }
            }
        }
    }
    public static void reiniciarValoresZn(double zn[],int n){
        z=0;
        for (int j = 0; j < n; j++){
            zn[j] = 0;
        }
    }
}
