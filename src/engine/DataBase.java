package engine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

    private final String url = "jdbc:sqlite:data/EcoHero.db";

    public DataBase() {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Score (" +
                         "ra TEXT PRIMARY KEY, " +
                         "nome TEXT NOT NULL, " +
                         "score INTEGER, " +
                         "tempo INTEGER, " +
                         "desempenho REAL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco: " + e.getMessage());
        }
    }

    // --- MÉTODO GRAVAR (INSERT) ---
    public void gravar(String ra, String nome, int score, int tempo) {
        String sql = "INSERT INTO Score(ra, nome, score, tempo, desempenho) VALUES(?,?,?,?,?)";
        float desempenho = (tempo > 0) ? (float) score / tempo : 0;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ra);
            pstmt.setString(2, nome);
            pstmt.setInt(3, score);
            pstmt.setInt(4, tempo);
            pstmt.setFloat(5, desempenho);
            pstmt.executeUpdate();
            System.out.println("Primeiro Record do jogador Gravado com sucesso");
        } catch (SQLException e) {
            // O código 19 no SQLite significa que o RA já existe (Constraint Violation)
            if (e.getErrorCode() == 19) {
                // Tentamos editar, mas o método só vai salvar se for um recorde!
                editarApenasSeForRecorde(ra, nome, score, tempo, desempenho);
            } else {
                System.err.println("Erro ao gravar: " + e.getMessage());
            }
        }
    }
    
    private void editarApenasSeForRecorde(String ra, String nome, int score, int tempo, float novoDesempenho) {
        String sql = "UPDATE Score SET nome = ?, score = ?, tempo = ?, desempenho = ? WHERE ra = ? AND ? > desempenho";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nome);
            pstmt.setInt(2, score);
            pstmt.setInt(3, tempo);
            pstmt.setFloat(4, novoDesempenho);
            pstmt.setString(5, ra);
            pstmt.setFloat(6, novoDesempenho); 
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                System.out.println("Parabéns! Novo recorde pessoal gravado para o RA: " + ra);
            } else {
                System.out.println("Partida concluída, mas você não superou seu recorde anterior. Pontuação descartada.");
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao tentar atualizar recorde: " + e.getMessage());
        }
    }

    // --- MÉTODO CONSULTAR (SELECT RANKING) ---
    public List<String[]> consultarRanking() {
        List<String[]> ranking = new ArrayList<>();
        String sql = "SELECT ra, nome, score, tempo, desempenho FROM Score ORDER BY desempenho DESC LIMIT 10";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ranking.add(new String[]{
                    rs.getString("ra"),
                    rs.getString("nome"),
                    String.valueOf(rs.getInt("score")),
                    String.valueOf(rs.getInt("tempo")),
                    String.format("%.2f", rs.getFloat("desempenho"))
                });
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return ranking;
    }

    // --- MÉTODO EDITAR (UPDATE) ---
    public void editar(String ra, String nome, int score, int tempo) {
        String sql = "UPDATE Score SET nome = ?, score = ?, tempo = ?, desempenho = ? WHERE ra = ?";
        float desempenho = (tempo > 0) ? (float) score / tempo : 0;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setInt(2, score);
            pstmt.setInt(3, tempo);
            pstmt.setFloat(4, desempenho);
            pstmt.setString(5, ra);
            pstmt.executeUpdate();
            System.out.println("Dados atualizados para o RA: " + ra);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    // --- MÉTODO APAGAR (DELETE) ---
    public void apagar(String ra) {
        String sql = "DELETE FROM Score WHERE ra = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ra);
            pstmt.executeUpdate();
            System.out.println("Registro removido!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
