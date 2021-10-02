function autocomplete(inp, arr) {
    /*the autocomplete function takes two arguments,
  the text field element and an array of possible autocompleted values:*/
    var currentFocus;
    /*execute a function when someone writes in the text field:*/
    inp.addEventListener("input", function(e) {
        var a, b, i, val = this.value;
        /*close any already open lists of autocompleted values*/
        closeAllLists();
        if (!val) { return false;}
        currentFocus = -1;
        /*create a DIV element that will contain the items (values):*/
        a = document.createElement("DIV");
        a.setAttribute("id", this.id + "autocomplete-list");
        a.setAttribute("class", "autocomplete-items");
        /*append the DIV element as a child of the autocomplete container:*/
        this.parentNode.appendChild(a);
        /*for each item in the array...*/
        for (i = 0; i < arr.length; i++) {
            /*check if the item starts with the same letters as the text field value:*/
            if (arr[i].substr(0, val.length).toUpperCase() == val.toUpperCase()) {
                /*create a DIV element for each matching element:*/
                b = document.createElement("DIV");
                /*make the matching letters bold:*/
                b.innerHTML = "<strong>" + arr[i].substr(0, val.length) + "</strong>";
                b.innerHTML += arr[i].substr(val.length);
                /*insert a input field that will hold the current array item's value:*/
                b.innerHTML += "<input type='hidden' value='" + arr[i] + "'>";
                /*execute a function when someone clicks on the item value (DIV element):*/
                b.addEventListener("click", function(e) {
                    /*insert the value for the autocomplete text field:*/
                    inp.value = this.getElementsByTagName("input")[0].value;
                    /*close the list of autocompleted values,
              (or any other open lists of autocompleted values:*/
                    closeAllLists();
                });
                a.appendChild(b);
            }
        }
    });
    /*execute a function presses a key on the keyboard:*/
    inp.addEventListener("keydown", function(e) {
        var x = document.getElementById(this.id + "autocomplete-list");
        if (x) x = x.getElementsByTagName("div");
        if (e.keyCode == 40) {
            /*If the arrow DOWN key is pressed,
        increase the currentFocus variable:*/
            currentFocus++;
            /*and and make the current item more visible:*/
            addActive(x);
        } else if (e.keyCode == 38) { //up
            /*If the arrow UP key is pressed,
        decrease the currentFocus variable:*/
            currentFocus--;
            /*and and make the current item more visible:*/
            addActive(x);
        } else if (e.keyCode == 13) {
            /*If the ENTER key is pressed, prevent the form from being submitted,*/
            e.preventDefault();
            if (currentFocus > -1) {
                /*and simulate a click on the "active" item:*/
                if (x) x[currentFocus].click();
            }
        }
    });
    function addActive(x) {
        /*a function to classify an item as "active":*/
        if (!x) return false;
        /*start by removing the "active" class on all items:*/
        removeActive(x);
        if (currentFocus >= x.length) currentFocus = 0;
        if (currentFocus < 0) currentFocus = (x.length - 1);
        /*add class "autocomplete-active":*/
        x[currentFocus].classList.add("autocomplete-active");
    }
    function removeActive(x) {
        /*a function to remove the "active" class from all autocomplete items:*/
        for (var i = 0; i < x.length; i++) {
            x[i].classList.remove("autocomplete-active");
        }
    }
    function closeAllLists(elmnt) {
        /*close all autocomplete lists in the document,
    except the one passed as an argument:*/
        var x = document.getElementsByClassName("autocomplete-items");
        for (var i = 0; i < x.length; i++) {
            if (elmnt != x[i] && elmnt != inp) {
                x[i].parentNode.removeChild(x[i]);
            }
        }
    }
    /*execute a function when someone clicks in the document:*/
    document.addEventListener("click", function (e) {
        closeAllLists(e.target);
    });
}


var concelhos = ["Águeda",
                 "Albergaria-a-Velha",
                 "Anadia",
                 "Arouca",
                 "Aveiro",
                 "Castelo de Paiva",
                 "Espinho",
                 "Estarreja",
                 "Ílhavo",
                 "Mealhada",
                 "Murtosa",
                 "Oliveira de Azeméis",
                 "Oliveira do Bairro",
                 "Ovar",
                 "Santa Maria da Feira",
                 "São João da Madeira",
                 "Sever do Vouga",
                 "Vagos",
                 "Vale de Cambra",
                 "Aljustrel",
                 "Almodôvar",
                 "Alvito",
                 "Barrancos",
                 "Beja",
                 "Castro Verde",
                 "Cuba",
                 "Ferreira do Alentejo",
                 "Mértola",
                 "Moura",
                 "Odemira",
                 "Ourique",
                 "Serpa",
                 "Vidigueira",
                 "Amares",
                 "Barcelos",
                 "Braga",
                 "Cabeceiras de Basto",
                 "Celorico de Basto",
                 "Esposende",
                 "Fafe",
                 "Guimarães",
                 "Póvoa de Lanhoso",
                 "Terras de Bouro",
                 "Vieira do Minho",
                 "Vila Nova de Famalicão",
                 "Vila Verde",
                 "Vizela",
                 "Alfândega da Fé",
                 "Bragança",
                 "Carrazeda de Ansiães",
                 "Freixo de Espada à Cinta",
                 "Macedo de Cavaleiros",
                 "Miranda do Douro",
                 "Mirandela",
                 "Mogadouro",
                 "Torre de Moncorvo",
                 "Vila Flor",
                 "Vimioso",
                 "Vinhais",
                 "Belmonte",
                 "Castelo Branco",
                 "Covilhã",
                 "Fundão",
                 "Idanha-a-Nova",
                 "Oleiros",
                 "Penamacor",
                 "Proença-a-Nova",
                 "Sertã",
                 "Vila de Rei",
                 "Vila Velha de Ródão",
                 "Arganil",
                 "Cantanhede",
                 "Coimbra",
                 "Condeixa-a-Nova",
                 "Figueira da Foz",
                 "Góis",
                 "Lousã",
                 "Mira",
                 "Miranda do Corvo",
                 "Montemor-o-Velho",
                 "Oliveira do Hospital",
                 "Pampilhosa da Serra",
                 "Penacova",
                 "Penela",
                 "Soure",
                 "Tábua",
                 "Vila Nova de Poiares",
                 "Alandroal",
                 "Arraiolos",
                 "Borba",
                 "Estremoz",
                 "Évora",
                 "Montemor-o-Novo",
                 "Mora",
                 "Mourão",
                 "Portel",
                 "Redondo",
                 "Reguengos de Monsaraz ",
                 "Vendas Novas",
                 "Viana do Alentejo",
                 "Vila Viçosa",
                 "Albufeira",
                 "Alcoutim",
                 "Aljezur",
                 "Castro Marim",
                 "Faro",
                 "Lagoa",
                 "Lagos",
                 "Loulé",
                 "Monchique",
                 "Olhão",
                 "Portimão",
                 "São Brás de Alportel",
                 "Silves",
                 "Tavira",
                 "Vila do Bispo",
                 "Vila Real de Santo António",
                 "Aguiar da Beira",
                 "Almeida",
                 "Celorico da Beira",
                 "Figueira de Castelo Rodrigo",
                 "Fornos de Algodres",
                 "Gouveia",
                 "Guarda",
                 "Manteigas",
                 "Mêda",
                 "Pinhel",
                 "Sabugal",
                 "Seia",
                 "Trancoso",
                 "Vila Nova de Foz Côa",
                 "Alcobaça",
                 "Alvaiázere",
                 "Ansião",
                 "Batalha",
                 "Bombarral",
                 "Caldas da Rainha",
                 "Castanheira de Pera",
                 "Figueiró dos Vinhos",
                 "Leiria",
                 "Marinha Grande",
                 "Nazaré",
                 "Óbidos",
                 "Pedrógão Grande",
                 "Peniche",
                 "Pombal",
                 "Porto de Mós",
                 "Alenquer",
                 "Amadora",
                 "Arruda dos Vinhos",
                 "Azambuja",
                 "Cadaval",
                 "Cascais",
                 "Lisboa",
                 "Loures",
                 "Lourinhã",
                 "Mafra",
                 "Odivelas",
                 "Oeiras",
                 "Sintra",
                 "Sobral de Monte Agraço",
                 "Torres Vedras",
                 "Vila Franca de Xira",
                 "Alter do Chão",
                 "Arronches",
                 "Avis",
                 "Campo Maior",
                 "Castelo de Vide",
                 "Crato",
                 "Elvas",
                 "Fronteira",
                 "Gavião",
                 "Marvão",
                 "Monforte",
                 "Nisa",
                 "Ponte de Sor",
                 "Portalegre",
                 "Sousel",
                 "Amarante",
                 "Baião",
                 "Felgueiras",
                 "Gondomar",
                 "Lousada",
                 "Maia",
                 "Marco de Canaveses",
                 "Matosinhos",
                 "Paços de Ferreira",
                 "Paredes",
                 "Penafiel",
                 "Porto",
                 "Póvoa de Varzim",
                 "Santo Tirso",
                 "Trofa",
                 "Valongo",
                 "Vila do Conde",
                 "Vila Nova de Gaia",
                 "Abrantes",
                 "Alcanena",
                 "Almeirim",
                 "Alpiarça",
                 "Benavente",
                 "Cartaxo",
                 "Chamusca",
                 "Constância",
                 "Coruche",
                 "Entroncamento",
                 "Ferreira do Zêzere",
                 "Golegã",
                 "Mação",
                 "Ourém",
                 "Rio Maior",
                 "Salvaterra de Magos",
                 "Santarém",
                 "Sardoal",
                 "Tomar",
                 "Torres Novas",
                 "Vila Nova da Barquinha",
                 "Alcácer do Sal",
                 "Alcochete",
                 "Almada",
                 "Barreiro",
                 "Grândola",
                 "Moita",
                 "Montijo",
                 "Palmela",
                 "Santiago do Cacém",
                 "Seixal",
                 "Sesimbra ",
                 "Setúbal",
                 "Sines",
                 "Arcos de Valdevez",
                 "Caminha",
                 "Melgaço",
                 "Monção",
                 "Paredes de Coura",
                 "Ponte da Barca",
                 "Ponte de Lima",
                 "Valença",
                 "Viana do Castelo",
                 "Vila Nova de Cerveira",
                 "Alijó",
                 "Boticas",
                 "Chaves",
                 "Mesão Frio",
                 "Mondim de Basto",
                 "Montalegre",
                 "Murça",
                 "Peso da Régua",
                 "Ribeira de Pena",
                 "Sabrosa",
                 "Santa Marta de Penaguião",
                 "Valpaços",
                 "Vila Pouca de Aguiar",
                 "Vila Real",
                 "Armamar",
                 "Carregal do Sal",
                 "Castro Daire",
                 "Cinfães",
                 "Lamego",
                 "Mangualde",
                 "Moimenta da Beira",
                 "Mortágua",
                 "Nelas",
                 "Oliveira de Frades",
                 "Penalva do Castelo",
                 "Penedono",
                 "Resende",
                 "Santa Comba Dão",
                 "São João da Pesqueira",
                 "São Pedro do Sul",
                 "Sátão",
                 "Sernancelhe",
                 "Tabuaço",
                 "Tarouca",
                 "Tondela",
                 "Vila Nova de Paiva",
                 "Viseu",
                 "Vouzela",
                 "Angra do Heroísmo",
                 "Calheta",
                 "Horta",
                 "Lagoa",
                 "Lajes das Flores",
                 "Lajes do Pico",
                 "Madalena",
                 "Nordeste",
                 "Ponta Delgada",
                 "Povoação",
                 "Praia da Vitória",
                 "Ribeira Grande",
                 "Santa Cruz da Graciosa",
                 "Santa Cruz das Flores",
                 "São Roque do Pico",
                 "Velas",
                 "Vila do Corvo",
                 "Vila do Porto",
                 "Vila Franca do Campo",
                 "Calheta",
                 "Câmara de Lobos",
                 "Ponta do Sol",
                 "Porto Moniz",
                 "Ribeira Brava",
                 "Funchal",
                 "Machico",
                 "Porto Santo",
                 "Santa Cruz",
                 "Santana",
                 "São Vicente"
                ];
/*initiate the autocomplete function on the "myInput" element, and pass along the countries array as possible autocomplete values:*/
autocomplete(document.getElementById("myInput"), concelhos);
