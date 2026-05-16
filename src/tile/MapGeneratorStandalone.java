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
        
        // 1. Instancia o GamePanel para carregar o LevelManager e o setupLevels() automaticamente
        GamePanel gp = new GamePanel();
        
        // 2. Cria a pasta de saída na raiz do projeto para não misturar com o src do jogo
        File diretorioSaida = new File("src/maps");
        if (!diretorioSaida.exists()) {
            diretorioSaida.mkdir();
        }

        // 3. Varre a lista de níveis configurada no seu LevelManager
        for (int i = 0; i < gp.levelM.levels.size(); i++) {
            
            // Pega o LevelConfig da fase atual
            LevelConfig lvl = gp.levelM.levels.get(i);
            
            String caminhoOriginal = lvl.mapPath; 
            String nomeArquivo = caminhoOriginal.substring(caminhoOriginal.lastIndexOf("/") + 1);
            
            // Pega as colunas e linhas exatas do LevelConfig obtido via LevelManager
            int colunas = lvl.maxCols; 
            int linhas = lvl.maxRows;  

            File arquivoMapa = new File(diretorioSaida, nomeArquivo);

            // --- CHECAGEM DE SEGURANÇA ---
            // Se o arquivo já existir na pasta "mapas_gerados", pula para não destruir seu progresso
            if (arquivoMapa.exists()) {
                System.out.println("⚠️  [Ignorado] O arquivo '" + nomeArquivo + "' já existe na pasta /" + diretorioSaida.getName());
                continue;
            }

            // --- GERAÇÃO DA MATRIZ DO MAPA ---
            System.out.println("🛠️  Criando matriz base para " + nomeArquivo + " [" + colunas + "x" + linhas + "]...");
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoMapa))) {
                
                for (int row = 0; row < linhas; row++) {
                    StringBuilder linhaTexto = new StringBuilder();

                    for (int col = 0; col < colunas; col++) {
                        int tileID = 0; // Padrão: Ar / Vazio (ID 0)

                        // 1. Chão (Última linha do mapa) -> ID 1
                        if (row == linhas - 1) {
                            tileID = 1;
                        }
                        // 2. Teto (Linha 0) OU Laterais (Coluna 0 ou Última Coluna) -> ID 2
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