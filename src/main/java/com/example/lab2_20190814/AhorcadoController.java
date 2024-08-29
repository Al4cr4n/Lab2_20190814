package com.example.lab2_20190814;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Controller
public class AhorcadoController {
    private Integer max=4;
    private Integer min=0;

    private List<String> animales = Arrays.asList("leon", "elefante", "tigre", "cebra", "jirafa");
    private List<String> frutas = Arrays.asList("manzana", "platano", "kiwi", "mango", "pera");
    private List<String> paises = Arrays.asList("Mexico", "Canada", "Brasil", "España", "Francia");

    private String palabraSeleccionada;
    private String palabraOculta;
    private int intentosRestantes;

    @GetMapping("/configuracion")
    public String configuracion(Model model) {
        model.addAttribute("ahorcadoConfig", new AhorcadoConfig());
        return "configuracion";
    }

    @PostMapping("/juego")
    public String juego(@ModelAttribute AhorcadoConfig ahorcadoConfig, Model model,  @RequestParam String tema) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        palabraSeleccionada = seleccionarPalabra(ahorcadoConfig);

        if (palabraSeleccionada == null) {
            model.addAttribute("mensajeError", "No hay palabras disponibles para esa longitud.");
            return "configuracion";
        }



        intentosRestantes = ahorcadoConfig.getMaxIntentos();
        palabraOculta = "_".repeat(palabraSeleccionada.length());

        model.addAttribute("palabra", palabraOculta);
        model.addAttribute("intentos", intentosRestantes);
        model.addAttribute("tema", ahorcadoConfig.getTema());

        return "juego";
    }

    @PostMapping("/adivinar")
    public String adivinar(@RequestParam("letra") String letra, Model model) {
        if (palabraSeleccionada.contains(letra)) {
            StringBuilder nuevaPalabraOculta = new StringBuilder(palabraOculta);

            for (int i = 0; i < palabraSeleccionada.length(); i++) {
                if (palabraSeleccionada.charAt(i) == letra.charAt(0)) {
                    nuevaPalabraOculta.setCharAt(i, letra.charAt(0));
                }
            }
            palabraOculta = nuevaPalabraOculta.toString();
        } else {
            intentosRestantes--;
        }

        model.addAttribute("palabra", palabraOculta);
        model.addAttribute("intentos", intentosRestantes);

        if (intentosRestantes == 0) {
            model.addAttribute("mensajeFinal", "Perdiste! La palabra era: " + palabraSeleccionada);
            return "resultado";
        }

        if (!palabraOculta.contains("_")) {
            model.addAttribute("mensajeFinal", "Ganaste! La palabra era: " + palabraSeleccionada);
            return "resultado";
        }

        return "juego";
    }

    private String seleccionarPalabra(AhorcadoConfig config) {
        List<String> palabras;

        switch (config.getTema()) {
            case "Animales":
                palabras = animales;
                break;
            case "Frutas":
                palabras = frutas;
                break;
            case "Países":
                palabras = paises;
                break;
            default:
                return null;
        }

        return palabras.stream()
                .filter(p -> p.length() == config.getLongitudPalabra())
                .findAny()
                .orElse(null);
    }
}