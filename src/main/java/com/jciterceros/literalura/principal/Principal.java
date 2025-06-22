package com.jciterceros.literalura.principal;

import com.jciterceros.literalura.model.Autor;
import com.jciterceros.literalura.model.Livro;
import com.jciterceros.literalura.service.LivroService;
import com.jciterceros.literalura.service.ConsumoAPI;
import com.jciterceros.literalura.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@Order(2)
public class Principal implements CommandLineRunner {

    @Autowired
    private LivroService livroService;

    @Autowired
    private ConsumoAPI consumoAPI;

    @Autowired
    private ConverteDados converteDados;

    private final Scanner scanner = new Scanner(System.in);
    private final String URL_BASE = "https://gutendex.com/books/";

    @Override
    public void run(String... args) throws Exception {
        exibirMenu();
    }

    private void exibirMenu() {
        while (true) {
            System.out.println("\n=== LITERALURA ===");
            System.out.println("1 - Buscar livros pelo título");
            System.out.println("2 - Listar livros registrados");
            System.out.println("3 - Listar autores registrados");
            System.out.println("4 - Listar autores vivos em um determinado ano");
            System.out.println("5 - Listar autores nascidos em determinado ano");
            System.out.println("6 - Listar autores por ano de sua morte");
            System.out.println("7 - Listar livros em um determinado idioma");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            try {
                String entrada = scanner.nextLine().trim();

                // Verificar se a entrada é um número
                if (!entrada.matches("\\d+")) {
                    System.out.println(" Entrada inválida! Digite apenas números.");
                    continue;
                }

                int opcao = Integer.parseInt(entrada);

                switch (opcao) {
                    case 1:
                        buscarLivroPorTitulo();
                        break;
                    case 2:
                        listarLivrosRegistrados();
                        break;
                    case 3:
                        listarAutoresRegistrados();
                        break;
                    case 4:
                        listarAutoresVivosEmAno();
                        break;
                    case 5:
                        listarAutoresNascidosEmAno();
                        break;
                    case 6:
                        listarAutoresPorAnoMorte();
                        break;
                    case 7:
                        listarLivrosPorIdioma();
                        break;
                    case 0:
                        System.out.println(" Saindo...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println(" Opção inválida! Digite um número entre 0 e 7.");
                }
            } catch (NumberFormatException e) {
                System.out.println(" Entrada inválida! Digite apenas números.");
            } catch (Exception e) {
                System.out.println(" Erro inesperado: " + e.getMessage());
            }
        }
    }

    private void buscarLivroPorTitulo() {
        System.out.print("Digite o título do livro: ");
        String titulo = scanner.nextLine();

        try {
            // Buscar na API
            String url = URL_BASE + "?search=" + titulo.replace(" ", "%20");
            System.out.println("Buscando em: " + url);

            String json = consumoAPI.obterDados(url);
            System.out.println("Resposta da API recebida. Tamanho: " + json.length() + " caracteres");

            // Converter para DTO
            var resposta = converteDados.obterDados(json, com.jciterceros.literalura.dto.RespostaDTO.class);
            System.out.println("Conversão realizada. Resultados encontrados: " + resposta.resultados().size());

            if (resposta.resultados().isEmpty()) {
                System.out.println("Nenhum livro encontrado!");
                return;
            }

            // Mostrar resultados
            System.out.println("\nLivros encontrados:");
            for (int i = 0; i < resposta.resultados().size(); i++) {
                com.jciterceros.literalura.dto.LivroDTO livro = resposta.resultados().get(i);
                System.out.printf("%d - %s (%s) - Downloads: %d%n",
                        i + 1, livro.titulo(), livro.idiomas().get(0), livro.numeroDownloads());

                // Mostrar dados dos autores
                if (livro.autores() != null && !livro.autores().isEmpty()) {
                    System.out.println("   Autores:");
                    for (var autor : livro.autores()) {
                        String anos = "";
                        if (autor.anoNascimento() != null && autor.anoFalecimento() != null) {
                            anos = String.format(" (%d-%d)", autor.anoNascimento(), autor.anoFalecimento());
                        } else if (autor.anoNascimento() != null) {
                            anos = String.format(" (n. %d)", autor.anoNascimento());
                        } else if (autor.anoFalecimento() != null) {
                            anos = String.format(" (fal. %d)", autor.anoFalecimento());
                        }
                        System.out.printf("    %s%s%n", autor.nome(), anos);
                    }
                } else {
                    System.out.println("   Autor: Não informado");
                }
                System.out.println(); // Linha em branco entre livros
            }

            // Salvar livro escolhido
            System.out.print("Escolha um livro para salvar (0 para cancelar): ");
            try {
                String entrada = scanner.nextLine().trim();

                // Verificar se a entrada é um número
                if (!entrada.matches("\\d+")) {
                    System.out.println("Entrada inválida! Digite apenas números.");
                    return;
                }

                int escolha = Integer.parseInt(entrada);

                if (escolha == 0) {
                    System.out.println("Operação cancelada.");
                    return;
                } else if (escolha > 0 && escolha <= resposta.resultados().size()) {
                    com.jciterceros.literalura.dto.LivroDTO livroEscolhido = resposta.resultados().get(escolha - 1);

                    Livro livroSalvo = livroService.salvarLivro(livroEscolhido);
                    System.out.println(" Livro salvo com sucesso: " + livroSalvo.getTitulo());

                    // Mostrar autores salvos
                    if (livroSalvo.getAutores() != null && !livroSalvo.getAutores().isEmpty()) {
                        System.out.println("   Autores salvos:");
                        livroSalvo.getAutores().forEach(autor -> {
                            String anos = "";
                            if (autor.getAnoNascimento() != null && autor.getAnoFalecimento() != null) {
                                anos = String.format(" (%d-%d)", autor.getAnoNascimento(), autor.getAnoFalecimento());
                            } else if (autor.getAnoNascimento() != null) {
                                anos = String.format(" (n. %d)", autor.getAnoNascimento());
                            } else if (autor.getAnoFalecimento() != null) {
                                anos = String.format(" (fal. %d)", autor.getAnoFalecimento());
                            }
                            System.out.printf("   • %s%s%n", autor.getNome(), anos);
                        });
                    }
                } else {
                    System.out.println(
                            " Opção inválida! Digite um número entre 1 e " + resposta.resultados().size() + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println(" Entrada inválida! Digite apenas números.");
            } catch (Exception e) {
                System.out.println(" Erro inesperado: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Erro ao buscar livro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listarLivrosRegistrados() {
        List<Livro> livros = livroService.buscarTodosLivros();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro registrado!");
            return;
        }

        System.out.println("\nLivros registrados:");
        livros.forEach(livro -> {
            System.out.printf("- %s (%s) - Downloads: %d%n",
                    livro.getTitulo(), livro.getIdioma(), livro.getNumeroDownloads());

            // Mostrar autores do livro
            if (livro.getAutores() != null && !livro.getAutores().isEmpty()) {
                System.out.println("  Autores:");
                livro.getAutores().forEach(autor -> {
                    String anos = "";
                    if (autor.getAnoNascimento() != null && autor.getAnoFalecimento() != null) {
                        anos = String.format(" (%d-%d)", autor.getAnoNascimento(), autor.getAnoFalecimento());
                    } else if (autor.getAnoNascimento() != null) {
                        anos = String.format(" (n. %d)", autor.getAnoNascimento());
                    } else if (autor.getAnoFalecimento() != null) {
                        anos = String.format(" (fal. %d)", autor.getAnoFalecimento());
                    }
                    System.out.printf("  • %s%s%n", autor.getNome(), anos);
                });
            } else {
                System.out.println("  Autor: Não informado");
            }
            System.out.println(); // Linha em branco entre livros
        });
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = livroService.buscarTodosAutores();
        if (autores.isEmpty()) {
            System.out.println("Nenhum autor registrado!");
            return;
        }

        System.out.println("\nAutores registrados:");
        autores.forEach(autor -> {
            String anos = "";
            if (autor.getAnoNascimento() != null && autor.getAnoFalecimento() != null) {
                anos = String.format(" (%d-%d)", autor.getAnoNascimento(), autor.getAnoFalecimento());
            } else if (autor.getAnoNascimento() != null) {
                anos = String.format(" (n. %d)", autor.getAnoNascimento());
            } else if (autor.getAnoFalecimento() != null) {
                anos = String.format(" (fal. %d)", autor.getAnoFalecimento());
            }
            System.out.printf("- %s%s%n", autor.getNome(), anos);
        });
    }

    private void listarAutoresVivosEmAno() {
        System.out.print("Digite o ano: ");
        try {
            String entrada = scanner.nextLine().trim();
            if (!entrada.matches("\\d+")) {
                System.out.println(" Entrada inválida! Digite apenas números.");
                return;
            }

            int ano = Integer.parseInt(entrada);
            List<Autor> autores = livroService.buscarAutoresVivosEmAno(ano);

            if (autores.isEmpty()) {
                System.out.println("Nenhum autor vivo encontrado para o ano " + ano + "!");
                return;
            }

            System.out.println("\nAutores vivos em " + ano + ":");
            autores.forEach(autor -> {
                String anos = "";
                if (autor.getAnoNascimento() != null && autor.getAnoFalecimento() != null) {
                    anos = String.format(" (%d-%d)", autor.getAnoNascimento(), autor.getAnoFalecimento());
                } else if (autor.getAnoNascimento() != null) {
                    anos = String.format(" (n. %d)", autor.getAnoNascimento());
                }
                System.out.printf("- %s%s%n", autor.getNome(), anos);
            });
        } catch (NumberFormatException e) {
            System.out.println(" Entrada inválida! Digite apenas números.");
        } catch (Exception e) {
            System.out.println(" Erro inesperado: " + e.getMessage());
        }
    }

    private void listarAutoresNascidosEmAno() {
        System.out.print("Digite o ano de nascimento: ");
        try {
            String entrada = scanner.nextLine().trim();
            if (!entrada.matches("\\d+")) {
                System.out.println(" Entrada inválida! Digite apenas números.");
                return;
            }

            int ano = Integer.parseInt(entrada);
            List<Autor> autores = livroService.buscarAutoresNascidosEmAno(ano);

            if (autores.isEmpty()) {
                System.out.println("Nenhum autor nascido em " + ano + " encontrado!");
                return;
            }

            System.out.println("\nAutores nascidos em " + ano + ":");
            autores.forEach(autor -> {
                String anos = "";
                if (autor.getAnoFalecimento() != null) {
                    anos = String.format(" (fal. %d)", autor.getAnoFalecimento());
                }
                System.out.printf("- %s%s%n", autor.getNome(), anos);
            });
        } catch (NumberFormatException e) {
            System.out.println(" Entrada inválida! Digite apenas números.");
        } catch (Exception e) {
            System.out.println(" Erro inesperado: " + e.getMessage());
        }
    }

    private void listarAutoresPorAnoMorte() {
        System.out.print("Digite o ano de falecimento: ");
        try {
            String entrada = scanner.nextLine().trim();
            if (!entrada.matches("\\d+")) {
                System.out.println(" Entrada inválida! Digite apenas números.");
                return;
            }

            int ano = Integer.parseInt(entrada);
            List<Autor> autores = livroService.buscarAutoresPorAnoMorte(ano);

            if (autores.isEmpty()) {
                System.out.println("Nenhum autor falecido em " + ano + " encontrado!");
                return;
            }

            System.out.println("\nAutores falecidos em " + ano + ":");
            autores.forEach(autor -> {
                String anos = "";
                if (autor.getAnoNascimento() != null) {
                    anos = String.format(" (n. %d)", autor.getAnoNascimento());
                }
                System.out.printf("- %s%s%n", autor.getNome(), anos);
            });
        } catch (NumberFormatException e) {
            System.out.println(" Entrada inválida! Digite apenas números.");
        } catch (Exception e) {
            System.out.println(" Erro inesperado: " + e.getMessage());
        }
    }

    private void listarLivrosPorIdioma() {
        List<String> idiomas = livroService.buscarIdiomasDisponiveis();
        if (idiomas.isEmpty()) {
            System.out.println("Nenhum idioma disponível!");
            return;
        }

        System.out.println("\nIdiomas disponíveis:");
        idiomas.forEach(System.out::println);

        System.out.print("Digite o idioma: ");
        String idioma = scanner.nextLine();

        List<Livro> livros = livroService.buscarPorIdioma(idioma);
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro encontrado para este idioma!");
            return;
        }

        System.out.println("\nLivros em " + idioma + ":");
        livros.forEach(livro -> {
            System.out.printf("- %s (Downloads: %d)%n",
                    livro.getTitulo(), livro.getNumeroDownloads());
        });
    }

    private void exibirEstatisticas() {
        List<Livro> top5 = livroService.buscarTop5Livros();
        System.out.println("\nTop 5 livros mais baixados:");
        top5.forEach(livro -> {
            System.out.printf("- %s: %d downloads%n",
                    livro.getTitulo(), livro.getNumeroDownloads());
        });
    }
}