import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static final int MAXSIZE = 100;
    public static void main(String[] args) {

        System.out.println("last id : " + GetLastId());
        int number = GetLastId() + 1;
        Proverbs[] proverbs = new Proverbs[MAXSIZE]; // MAXSIZE = 100
        GetProverbs(proverbs,number);

        System.out.println("==명언 앱==");
        System.out.print("명령)");
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        while(!command.equals("종료")) {
            if(command.equals("등록")){
                System.out.print("명언 : ");
                String proverb = scanner.nextLine();
                System.out.print("작가 : ");
                String author = scanner.nextLine();
                System.out.println(number + "번 명언이 등록되었습니다.");

                proverbs[number-1] = new Proverbs(number, proverb, author);

                AddLastIdFile(number);
                AddFile(number,proverbs);
                number++;
            }
            else if(command.equals("목록")){
                System.out.println("번호 / 작가 / 명언\n----------------------");
                for(int i=number-1;i>=0;i--){
                    if(proverbs[i] == null)
                        continue;
                    System.out.println(proverbs[i].getNumber() + " / " + proverbs[i].getAuthor() + " / " + proverbs[i].getProverb());
                }
            }
            else if(command.contains("삭제")){
                String numbersOnly = command.replaceAll("[^0-9]", "");
                int num = Integer.parseInt(numbersOnly);
                // OOB or not exist or already deleted
                if(num<1 || proverbs[num-1]==null){
                    System.out.println(num + "번 명언은 존재하지 않습니다");
                }
                else{
                    proverbs[num-1]=null;
                    DeleteFile(num,proverbs);
                    System.out.println(num + "번 명언이 삭제되었습니다");
                }
            }
            else if(command.contains("수정")){
                String numbersOnly = command.replaceAll("[^0-9]", "");
                int num = Integer.parseInt(numbersOnly);
                if(num<1 || proverbs[num-1]==null){
                    System.out.println(num + "번 명언은 존재하지 않습니다");
                }
                else{
                    System.out.println("명언(기존) " + proverbs[num-1].getProverb());
                    System.out.print("명언 : ");
                    String proverb = scanner.nextLine();
                    proverbs[num-1].setProverb(proverb);

                    System.out.println("작가(기존) " + proverbs[num-1].getAuthor());
                    System.out.print("작가 : ");
                    String author = scanner.nextLine();
                    proverbs[num-1].setAuthor(author);
                    AddFile(num,proverbs);
                }
            }
            else if (command.contains("빌드")){
                BuildFile(proverbs,number);
            }
            System.out.print("명령)");
            command = scanner.nextLine();
        }
    }


    // 빌드
    public static void BuildFile(Proverbs[] proverbs, int number){
        try {
            File dir = new File("./db/wiseSaying");
            if (!dir.exists()) dir.mkdirs(); // 파일이 없을 경우 생성
            StringBuilder outputString = new StringBuilder("[");
            OutputStream output = new FileOutputStream("./db/wiseSaying/data.json");
            boolean first = true;
            for(int i=1;i<=number;i++){
                if(proverbs[i-1]!=null){
                    if(!first){
                        outputString.append(",\n");
                    }
                    first = false;
                    int id = proverbs[i-1].getNumber();
                    String proverb = proverbs[i-1].getProverb();
                    String author = proverbs[i-1].getAuthor();
                    outputString.append("{\n\t\"id\" : ").append(id).append(",\n\t\"proverb\":\"").append(proverb).append("\",\n\t\"author\":\"").append(author).append("\"\n}");
                }
            }
            outputString.append(']');
            //DEBUG
            System.out.println("빌드 String: " + outputString);
            output.write(outputString.toString().getBytes());
            output.close();
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    // 파일 삭제
    public static void DeleteFile(int number, Proverbs[] proverbs) {
        try{
            File file = new File("./db/wiseSaying"+ number + ".json");
            if(file.exists()){
                file.delete();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    // 파일 추가
    public static void AddFile(int number, Proverbs[] proverbs) {
        try {
            File dir = new File("./db/wiseSaying");
            if (!dir.exists()) dir.mkdirs(); // 파일이 없을 경우 생성
            OutputStream output = new FileOutputStream("./db/wiseSaying/"+number+".json");
            String proverb = proverbs[number-1].getProverb();
            String author = proverbs[number-1].getAuthor();
            String outputString = "{\n\t\"id\" : " + number + ",\n\t\"proverb\":\""+proverb+"\",\n\t\"author\":\""+author+"\"\n}";

            //DEBUG
            System.out.println("저장하는 String: " + outputString);

            output.write(outputString.getBytes());
            output.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static void AddLastIdFile(int number) {
        try{
            File dir = new File("./db/wiseSaying");
            if (!dir.exists()) dir.mkdirs();
            OutputStream output = new FileOutputStream("./db/wiseSaying/lastId.txt");

            //DEBUG
//            System.out.println("저장한 마지막 번호 " + number);

            output.write(String.valueOf(number).getBytes());
            output.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static int GetLastId() {
        try{
            File dir = new File("./db/wiseSaying");
            if (!dir.exists()) dir.mkdirs();
            BufferedReader reader = new BufferedReader(new FileReader("./db/wiseSaying/lastId.txt"));
            String line = reader.readLine();
            reader.close();

//            DEBUG
//            System.out.println("가져온 마지막 ID " + line);

            return Integer.parseInt(line);


        }catch(Exception e){
            e.getStackTrace();
        }
        return 0;
    }

    public static void GetProverbs(Proverbs[] proverbs,int number) {
        try{
            for(int i=1;i<number;i++){
                String fileName = "./db/wiseSaying/"+i+".json";
                File file = new File(fileName);
                if(file.exists()){
                    //DEBUG
                    System.out.println("파일을 찾음, 파일주소 :" + fileName );
                    proverbs[i-1] = readJsonFile(fileName);
                }
            }
        }catch (Exception e){
            e.getStackTrace();
        }
    }

    public static Proverbs readJsonFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim());
            }

            String json = sb.toString();

            //DEBUG
            System.out.println("읽은 json 파일" + json);

            return parseProverbs(json);

        } catch (IOException e) {
            System.out.println("에러 발생");
            e.printStackTrace();
            return null;
        }
    }
    public static Proverbs parseProverbs(String json) {
        Proverbs p = new Proverbs();

        // id
        String idStr = json.split("\"id\"\\s*:\\s*")[1].split(",")[0].trim();
        p.setNumber(Integer.parseInt(idStr));

        // proverb
        String proverbStr = json.split("\"proverb\"\\s*:\\s*\"")[1].split("\"")[0];
        p.setProverb(proverbStr);

        // author
        String authorStr = json.split("\"author\"\\s*:\\s*\"")[1].split("\"")[0];
        p.setAuthor(authorStr);

        //DEBUG
        System.out.println(p.getNumber() + " " + p.getProverb() + " " + p.getAuthor() + " ");
        return p;
    }
}

class Proverbs{
    int number;
    String proverb;
    String author;

    public Proverbs(){
        number = 0;
        proverb = "";
        author = "작자미상";
    }

    public Proverbs(int number, String proverb, String author){
        this.number = number;
        this.proverb = proverb;
        this.author = author;
    }

    public int getNumber(){
        return number;
    }
    public String getProverb(){
        return proverb;
    }
    public String getAuthor(){
        return author;
    }

    public void setNumber(int number){
        this.number = number;
    }
    public void setProverb(String proverb){
        this.proverb = proverb;
    }
    public void setAuthor(String author){
        this.author = author;
    }
}
