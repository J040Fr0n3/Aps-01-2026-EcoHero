package tile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import engine.GamePanel;
import engine.LevelConfig;

public class MapGeneratorStandalone {

    public static void main(String[] args) {
        System.out.println("=== GERADOR AUTOMÁTICO DE MAPAS (ECOHERO) ===");
        
        GamePanel gp = new GamePanel();
        File diretorioSaida = new File("src/maps");
        if (!diretorioSaida.exists()) {
            diretorioSaida.mkdir();
        }
        for (int i = 0; i < gp.levelM.levels.size(); i++) {

            LevelConfig lvl = gp.levelM.levels.get(i);
            
            String caminhoOriginal = lvl.mapPath; 
            String nomeArquivo = caminhoOriginal.substring(caminhoOriginal.lastIndexOf("/") + 1);
            

            int colunas = lvl.maxCols; 
            int linhas = lvl.maxRows;  

            File arquivoMapa = new File(diretorioSaida, nomeArquivo);


            if (arquivoMapa.exists()) {
                System.out.println("⚠️  [Ignorado] O arquivo '" + nomeArquivo + "' já existe na pasta /" + diretorioSaida.getName());
                continue;
            }

            System.out.println("🛠️  Criando matriz base para " + nomeArquivo + " [" + colunas + "x" + linhas + "]...");
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoMapa))) {
                
                for (int row = 0; row < linhas; row++) {
                    StringBuilder linhaTexto = new StringBuilder();

                    for (int col = 0; col < colunas; col++) {
                        int tileID = 0; 


                        if (row == linhas - 1) {
                            tileID = 1;
                        }

                        else if (row == 0 || col == 0 || col == colunas - 1) {
                            tileID = 2;
                        }

                        linhaTexto.append(tileID);
                        

                        if (col < colunas - 1) {
                            linhaTexto.append(" "); 
                        }
                    }

                    bw.write(linhaTexto.toString());
                    
                    // Pula de linha no arquivo, exceto no final da matriz
                    if (row < linhas - 1) {
                        bw.newLine();
                    }
                }
                System.out.println("✅  Arquivo '" + nomeArquivo + "' gerado!");

            } catch (IOException e) {
                System.err.println("❌ Erro fatal ao escrever " + nomeArquivo + ": " + e.getMessage());
            }
        }
        
        System.out.println("\n=== PROCESSO FINALIZADO ===");
        System.out.println("Os mapas foram salvos com sucesso em: " + diretorioSaida.getAbsolutePath());
    }
}