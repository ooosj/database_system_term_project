import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://192.168.56.2:4567/club", "osj", "qwe123");
            Statement stmt = con.createStatement();

            while(true) {
                System.out.println("\n=== MySQL Database Program ===");
                System.out.println("1. 동아리 정보 검색  2. 동아리 추가");
                System.out.println("3. 동아리 정보 수정  4. 동아리 삭제");
                System.out.println("5. 학생 정보 검색  6. 학생 추가");
                System.out.println("7. 학생 정보 수정  8. 학생 정보 삭제");
                System.out.println("9. 게시물 검색  10. 게시물 추가");
                System.out.println("11. 게시물 수정  12. 게시물 삭제");


                System.out.println("99. 종료");
                System.out.print("선택하세요: ");

                int option = scanner.nextInt();

                if (option == 1) {
                    System.out.println("\n--- 동아리 정보 검색 ---");
                    System.out.println("1. 동아리명으로 검색하기");
                    System.out.println("2. 전체 동아리 검색하기");
                    System.out.print("선택하세요: ");
                    int searchOption = scanner.nextInt();

                    if (searchOption == 1) {
                        System.out.print("검색할 동아리명을 입력하세요: ");
                        scanner.nextLine(); // 버퍼 비우기
                        String clubName = scanner.nextLine();
                        String query = "SELECT * FROM 동아리 WHERE 이름 = '" + clubName + "'";
                        ResultSet rs = stmt.executeQuery(query);

                        System.out.println("\n[검색 결과]");
                        boolean found = false;
                        while (rs.next()) {
                            found = true;
                            System.out.println("동아리 ID: " + rs.getInt("동아리id"));
                            System.out.println("동아리 이름: " + rs.getString("동아리명"));
                            System.out.println("동아리 소개: " + rs.getString("소개"));
                            System.out.println("동아리 인원수: " + rs.getInt("동아리_인원수"));
                            System.out.println("------------------------");
                        }

                        if (!found) {
                            System.out.println("해당 동아리가 존재하지 않습니다.");
                        }
                    }
                    else if (searchOption == 2) {
                        String query = "SELECT * FROM 동아리";
                        ResultSet rs = stmt.executeQuery(query);

                        System.out.println("\n[전체 동아리 목록]");
                        while (rs.next()) {
                            System.out.println("동아리 ID: " + rs.getInt("동아리id"));
                            System.out.println("동아리 이름: " + rs.getString("동아리명"));
                            System.out.println("동아리 소개: " + rs.getString("소개"));
                            System.out.println("동아리 인원수: " + rs.getInt("동아리_인원수"));
                            System.out.println("------------------------");
                        }
                    }
                    else if (searchOption == 3){
                        // 동아리 소속 인원 전체 출력
                    }
                    else {
                        System.out.println("잘못된 선택입니다.");
                    }
                }

                else if (option == 2) {
                    System.out.println("\n--- 동아리 추가 ---");
                    scanner.nextLine();

                    System.out.print("동아리명을 입력하세요: ");
                    String clubName = scanner.nextLine();

                    System.out.print("소개를 입력하세요: ");
                    String description = scanner.nextLine();

                    System.out.print("인원수를 입력하세요: ");
                    int memberCount = scanner.nextInt();

                    String insertQuery = "INSERT INTO 동아리 (동아리명, 소개, 동아리_인원수) VALUES (?, ?, ?)";
                    PreparedStatement pstmt = con.prepareStatement(insertQuery);

                    pstmt.setString(1, clubName);
                    pstmt.setString(2, description);
                    pstmt.setInt(3, memberCount);

                    int rowsInserted = pstmt.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("동아리 정보가 성공적으로 추가되었습니다!");
                    } else {
                        System.out.println("동아리 정보 추가에 실패했습니다.");
                    }
                }

                else if (option == 3) {
                    System.out.println("\n--- 동아리 정보 수정 ---");
                    System.out.print("수정할 동아리명을 입력하세요: ");
                    scanner.nextLine();
                    String clubName = scanner.nextLine();

                    // 수정할 정보 입력받기
                    System.out.print("새로운 동아리명을 입력하세요 (현재 값을 유지하려면 엔터): ");
                    String newClubName = scanner.nextLine();

                    System.out.print("새로운 소개를 입력하세요 (현재 값을 유지하려면 엔터): ");
                    String newDescription = scanner.nextLine();

                    System.out.print("새로운 인원수를 입력하세요 (현재 값을 유지하려면 -1): ");
                    int newMemberCount = scanner.nextInt();

                    String updateQuery = "UPDATE 동아리 SET " +
                            (newClubName.isEmpty() ? "" : "동아리명 = ?, ") +
                            (newDescription.isEmpty() ? "" : "소개 = ?, ") +
                            (newMemberCount == -1 ? "" : "동아리_인원수 = ? ") +
                            "WHERE 동아리명 = ?";

                    // 마지막 ',' 제거
                    if (updateQuery.contains(", WHERE")) {
                        updateQuery = updateQuery.replace(", WHERE", " WHERE");
                    }

                    try (PreparedStatement pstmt = con.prepareStatement(updateQuery)) {
                        int paramIndex = 1;

                        // 동적으로 매핑
                        if (!newClubName.isEmpty()) {
                            pstmt.setString(paramIndex++, newClubName);
                        }
                        if (!newDescription.isEmpty()) {
                            pstmt.setString(paramIndex++, newDescription);
                        }
                        if (newMemberCount != -1) {
                            pstmt.setInt(paramIndex++, newMemberCount);
                        }
                        pstmt.setString(paramIndex, clubName);

                        int rowsUpdated = pstmt.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("동아리 정보가 성공적으로 수정되었습니다!");
                        } else {
                            System.out.println("수정할 동아리가 존재하지 않습니다.");
                        }
                    }
                }


                else if (option == 4) {
                    System.out.println("\n--- 동아리 삭제 ---");
                    System.out.print("삭제할 동아리명을 입력하세요: ");
                    scanner.nextLine();
                    String clubName = scanner.nextLine();

                    System.out.print("정말 삭제하시겠습니까?(Y/N) ");
                    String del = scanner.nextLine();

                    if(del.equals("y")|| del.equals("Y")) {
                        String deleteQuery = "DELETE FROM 동아리 WHERE 동아리명 = ?";
                        try (PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {
                            pstmt.setString(1, clubName);

                            int rowsDeleted = pstmt.executeUpdate();
                            if (rowsDeleted > 0) {
                                System.out.println("동아리가 성공적으로 삭제되었습니다!");
                            } else {
                                System.out.println("삭제할 동아리가 존재하지 않습니다.");
                            }
                        }
                    }
                    else {
                        System.out.println("동아리 삭제가 취소되었습니다.");
                    }

                }
                else if (option == 5) { // 학생 정보 검색
                    System.out.println("\n--- 학생 정보 검색 ---");
                    System.out.println("1. 이름으로 검색");
                    System.out.println("2. 학번으로 검색");
                    System.out.println("3. 동아리로 검색");
                    System.out.print("선택하세요: ");
                    int searchOption = scanner.nextInt();
                    scanner.nextLine(); // 버퍼 비우기

                    String query = "";
                    if (searchOption == 1) {
                        System.out.print("검색할 이름을 입력하세요: ");
                        String name = scanner.nextLine();
                        query = "SELECT 사용자.사용자id, 사용자.이름, 동아리.동아리명, 사용자.학번, 사용자.권한 " +
                                "FROM 사용자 " +
                                "LEFT JOIN 동아리 ON 사용자.동아리id = 동아리.동아리id " +
                                "WHERE 사용자.이름 = ?";
                        try (PreparedStatement pstmt = con.prepareStatement(query)) {
                            pstmt.setString(1, name);
                            ResultSet rs = pstmt.executeQuery();
                            System.out.println("-------------------------");
                            while (rs.next()) {
                                System.out.println("사용자ID: " + rs.getInt("사용자id"));
                                System.out.println("이름: " + rs.getString("이름"));
                                System.out.println("동아리명: " + rs.getString("동아리명")); // 동아리명 출력
                                System.out.println("학번: " + rs.getString("학번"));
                                System.out.println("권한: " + rs.getString("권한"));
                                System.out.println("-------------------------");
                            }
                        }
                    } else if (searchOption == 2) {
                        System.out.print("검색할 학번을 입력하세요: ");
                        String studentNumber = scanner.nextLine();
                        query = "SELECT 사용자.사용자id, 사용자.이름, 동아리.동아리명, 사용자.학번, 사용자.권한 " +
                                "FROM 사용자 " +
                                "LEFT JOIN 동아리 ON 사용자.동아리id = 동아리.동아리id " +
                                "WHERE 사용자.학번 = ?";
                        try (PreparedStatement pstmt = con.prepareStatement(query)) {
                            pstmt.setString(1, studentNumber);
                            ResultSet rs = pstmt.executeQuery();
                            while (rs.next()) {
                                System.out.println("사용자ID: " + rs.getInt("사용자id"));
                                System.out.println("이름: " + rs.getString("이름"));
                                System.out.println("동아리명: " + rs.getString("동아리명")); // 동아리명 출력
                                System.out.println("학번: " + rs.getString("학번"));
                                System.out.println("권한: " + rs.getString("권한"));
                                System.out.println("-------------------------");
                            }
                        }
                    } else if (searchOption == 3) {
                        System.out.print("검색할 동아리명을 입력하세요: ");
                        String clubName = scanner.nextLine();
                        query = "SELECT 사용자.사용자id, 사용자.이름, 동아리.동아리명, 사용자.학번, 사용자.권한 " +
                                "FROM 사용자 " +
                                "LEFT JOIN 동아리 ON 사용자.동아리id = 동아리.동아리id " +
                                "WHERE 동아리.동아리명 = ?";
                        try (PreparedStatement pstmt = con.prepareStatement(query)) {
                            pstmt.setString(1, clubName);
                            ResultSet rs = pstmt.executeQuery();
                            while (rs.next()) {
                                System.out.println("사용자ID: " + rs.getInt("사용자id"));
                                System.out.println("이름: " + rs.getString("이름"));
                                System.out.println("동아리명: " + rs.getString("동아리명")); // 동아리명 출력
                                System.out.println("학번: " + rs.getString("학번"));
                                System.out.println("권한: " + rs.getString("권한"));
                                System.out.println("-------------------------");
                            }
                        }
                    } else {
                        System.out.println("잘못된 선택입니다.");
                    }
                }


                else if (option == 6) { // 학생 추가
                    System.out.println("\n--- 학생 정보 추가 ---");
                    scanner.nextLine(); // 버퍼 비우기
                    System.out.print("이름을 입력하세요: ");
                    String name = scanner.nextLine();

                    System.out.print("동아리명을 입력하세요 (없으면 '없음' 입력): ");
                    String clubName = scanner.nextLine();

                    System.out.print("학번을 입력하세요: ");
                    String studentNumber = scanner.nextLine();

                    System.out.print("권한을 입력하세요 (학생/관리자): ");
                    String role = scanner.nextLine();

                    int clubId = 0; // 기본값으로 0 설정 (동아리가 없을 경우)
                    if (!clubName.equals("없음")) { // 동아리명이 '없음'이 아니면 동아리id 조회
                        String selectQuery = "SELECT 동아리id FROM 동아리 WHERE 동아리명 = ?";
                        try (PreparedStatement pstmt = con.prepareStatement(selectQuery)) {
                            pstmt.setString(1, clubName);
                            ResultSet rs = pstmt.executeQuery();
                            if (rs.next()) {
                                clubId = rs.getInt("동아리id");
                            } else {
                                System.out.println("입력하신 동아리명이 존재하지 않습니다.");
                                continue;
                            }
                        }
                    }

                    // 학생 정보 추가
                    String insertQuery = "INSERT INTO 사용자 (이름, 동아리id, 학번, 권한) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pstmt = con.prepareStatement(insertQuery)) {
                        pstmt.setString(1, name);
                        if (clubId == 0) {
                            pstmt.setNull(2, java.sql.Types.INTEGER); // 동아리ID가 없을 경우 NULL로 설정
                        } else {
                            pstmt.setInt(2, clubId);
                        }
                        pstmt.setString(3, studentNumber);
                        pstmt.setString(4, role);

                        int rowsInserted = pstmt.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("학생 정보가 성공적으로 추가되었습니다!");
                        } else {
                            System.out.println("학생 추가에 실패했습니다.");
                        }
                    }
                }

                else if (option == 7) { // 학생 정보 수정
                    System.out.println("\n--- 학생 정보 수정 ---");

                    scanner.nextLine();
                    System.out.print("수정할 학생의 이름을 입력하세요: ");
                    String userName = scanner.nextLine();

                    // 수정할 학생의 정보가 존재하는지 확인
                    String checkQuery = "SELECT 사용자id FROM 사용자 WHERE 이름 = ?";
                    int userId = -1;

                    try (PreparedStatement pstmt = con.prepareStatement(checkQuery)) {
                        pstmt.setString(1, userName);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            userId = rs.getInt("사용자id");
                        } else {
                            System.out.println("해당 이름의 학생이 존재하지 않습니다.");
                            continue;
                        }
                    }

                    // 수정할 새 정보를 입력받음
                    System.out.print("새로운 이름을 입력하세요 (현재 값을 유지하려면 엔터): ");
                    String newName = scanner.nextLine();

                    System.out.print("새로운 동아리명을 입력하세요 (현재 값을 유지하려면 엔터): ");
                    String newClubName = scanner.nextLine();

                    System.out.print("새로운 학번을 입력하세요 (현재 값을 유지하려면 엔터): ");
                    String newStudentNumber = scanner.nextLine();

                    System.out.print("새로운 권한을 입력하세요 (현재 값을 유지하려면 엔터): ");
                    String newRole = scanner.nextLine();

                    Integer newClubId = null; // 동아리id 초기화

                    // 동아리명을 입력한 경우 동아리id 조회
                    if (!newClubName.isEmpty()) {
                        String selectClubIdQuery = "SELECT 동아리id FROM 동아리 WHERE 동아리명 = ?";
                        try (PreparedStatement pstmt = con.prepareStatement(selectClubIdQuery)) {
                            pstmt.setString(1, newClubName);
                            ResultSet rs = pstmt.executeQuery();
                            if (rs.next()) {
                                newClubId = rs.getInt("동아리id");
                            } else {
                                System.out.println("입력하신 동아리명이 존재하지 않습니다.");
                                continue;
                            }
                        }
                    }

                    // 업데이트 쿼리 생성
                    String updateQuery = "UPDATE 사용자 SET " +
                            (newName.isEmpty() ? "" : "이름 = ?, ") +
                            (newClubId == null ? "" : "동아리id = ?, ") +
                            (newStudentNumber.isEmpty() ? "" : "학번 = ?, ") +
                            (newRole.isEmpty() ? "" : "권한 = ? ") +
                            "WHERE 사용자id = ?";

                    // 쿼리 형식 정리
                    if (updateQuery.contains(", WHERE")) {
                        updateQuery = updateQuery.replace(", WHERE", " WHERE");
                    }

                    // 업데이트 실행
                    try (PreparedStatement pstmt = con.prepareStatement(updateQuery)) {
                        int paramIndex = 1;

                        if (!newName.isEmpty()) {
                            pstmt.setString(paramIndex++, newName);
                        }
                        if (newClubId != null) {
                            pstmt.setInt(paramIndex++, newClubId);
                        }
                        if (!newStudentNumber.isEmpty()) {
                            pstmt.setString(paramIndex++, newStudentNumber);
                        }
                        if (!newRole.isEmpty()) {
                            pstmt.setString(paramIndex++, newRole);
                        }
                        pstmt.setInt(paramIndex, userId);

                        int rowsUpdated = pstmt.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("학생 정보가 성공적으로 수정되었습니다!");
                        } else {
                            System.out.println("수정할 학생 정보가 존재하지 않습니다.");
                        }
                    }
                }



                else if (option == 8) { // 학생 정보 삭제
                    System.out.println("\n--- 학생 정보 삭제 ---");
                    scanner.nextLine();
                    System.out.print("삭제할 학생의 이름을 입력하세요: ");
                    String userName = scanner.nextLine();

                    System.out.print("정말 삭제하시겠습니까? (Y/N): ");
                    String del = scanner.nextLine();

                    if (del.equals("Y") || del.equals("y")) {
                        String deleteQuery = "DELETE FROM 사용자 WHERE 이름 = ?";
                        try (PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {
                            pstmt.setString(1, userName);

                            int rowsDeleted = pstmt.executeUpdate();
                            if (rowsDeleted > 0) {
                                System.out.println("학생 정보가 성공적으로 삭제되었습니다!");
                            } else {
                                System.out.println("삭제할 학생 정보가 존재하지 않습니다.");
                            }
                        }
                    } else {
                        System.out.println("학생 정보 삭제가 취소되었습니다.");
                    }
                }



                else if (option == 99){
                    System.out.println("프로그램을 종료합니다.");
                    break;
                }
            }


        }catch (Exception e){

        }

    }
}