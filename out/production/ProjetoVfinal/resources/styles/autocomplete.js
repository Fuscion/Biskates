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


var concelhos = ["??gueda",
                 "Albergaria-a-Velha",
                 "Anadia",
                 "Arouca",
                 "Aveiro",
                 "Castelo de Paiva",
                 "Espinho",
                 "Estarreja",
                 "??lhavo",
                 "Mealhada",
                 "Murtosa",
                 "Oliveira de Azem??is",
                 "Oliveira do Bairro",
                 "Ovar",
                 "Santa Maria da Feira",
                 "S??o Jo??o da Madeira",
                 "Sever do Vouga",
                 "Vagos",
                 "Vale de Cambra",
                 "Aljustrel",
                 "Almod??var",
                 "Alvito",
                 "Barrancos",
                 "Beja",
                 "Castro Verde",
                 "Cuba",
                 "Ferreira do Alentejo",
                 "M??rtola",
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
                 "Guimar??es",
                 "P??voa de Lanhoso",
                 "Terras de Bouro",
                 "Vieira do Minho",
                 "Vila Nova de Famalic??o",
                 "Vila Verde",
                 "Vizela",
                 "Alf??ndega da F??",
                 "Bragan??a",
                 "Carrazeda de Ansi??es",
                 "Freixo de Espada ?? Cinta",
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
                 "Covilh??",
                 "Fund??o",
                 "Idanha-a-Nova",
                 "Oleiros",
                 "Penamacor",
                 "Proen??a-a-Nova",
                 "Sert??",
                 "Vila de Rei",
                 "Vila Velha de R??d??o",
                 "Arganil",
                 "Cantanhede",
                 "Coimbra",
                 "Condeixa-a-Nova",
                 "Figueira da Foz",
                 "G??is",
                 "Lous??",
                 "Mira",
                 "Miranda do Corvo",
                 "Montemor-o-Velho",
                 "Oliveira do Hospital",
                 "Pampilhosa da Serra",
                 "Penacova",
                 "Penela",
                 "Soure",
                 "T??bua",
                 "Vila Nova de Poiares",
                 "Alandroal",
                 "Arraiolos",
                 "Borba",
                 "Estremoz",
                 "??vora",
                 "Montemor-o-Novo",
                 "Mora",
                 "Mour??o",
                 "Portel",
                 "Redondo",
                 "Reguengos de Monsaraz ",
                 "Vendas Novas",
                 "Viana do Alentejo",
                 "Vila Vi??osa",
                 "Albufeira",
                 "Alcoutim",
                 "Aljezur",
                 "Castro Marim",
                 "Faro",
                 "Lagoa",
                 "Lagos",
                 "Loul??",
                 "Monchique",
                 "Olh??o",
                 "Portim??o",
                 "S??o Br??s de Alportel",
                 "Silves",
                 "Tavira",
                 "Vila do Bispo",
                 "Vila Real de Santo Ant??nio",
                 "Aguiar da Beira",
                 "Almeida",
                 "Celorico da Beira",
                 "Figueira de Castelo Rodrigo",
                 "Fornos de Algodres",
                 "Gouveia",
                 "Guarda",
                 "Manteigas",
                 "M??da",
                 "Pinhel",
                 "Sabugal",
                 "Seia",
                 "Trancoso",
                 "Vila Nova de Foz C??a",
                 "Alcoba??a",
                 "Alvai??zere",
                 "Ansi??o",
                 "Batalha",
                 "Bombarral",
                 "Caldas da Rainha",
                 "Castanheira de Pera",
                 "Figueir?? dos Vinhos",
                 "Leiria",
                 "Marinha Grande",
                 "Nazar??",
                 "??bidos",
                 "Pedr??g??o Grande",
                 "Peniche",
                 "Pombal",
                 "Porto de M??s",
                 "Alenquer",
                 "Amadora",
                 "Arruda dos Vinhos",
                 "Azambuja",
                 "Cadaval",
                 "Cascais",
                 "Lisboa",
                 "Loures",
                 "Lourinh??",
                 "Mafra",
                 "Odivelas",
                 "Oeiras",
                 "Sintra",
                 "Sobral de Monte Agra??o",
                 "Torres Vedras",
                 "Vila Franca de Xira",
                 "Alter do Ch??o",
                 "Arronches",
                 "Avis",
                 "Campo Maior",
                 "Castelo de Vide",
                 "Crato",
                 "Elvas",
                 "Fronteira",
                 "Gavi??o",
                 "Marv??o",
                 "Monforte",
                 "Nisa",
                 "Ponte de Sor",
                 "Portalegre",
                 "Sousel",
                 "Amarante",
                 "Bai??o",
                 "Felgueiras",
                 "Gondomar",
                 "Lousada",
                 "Maia",
                 "Marco de Canaveses",
                 "Matosinhos",
                 "Pa??os de Ferreira",
                 "Paredes",
                 "Penafiel",
                 "Porto",
                 "P??voa de Varzim",
                 "Santo Tirso",
                 "Trofa",
                 "Valongo",
                 "Vila do Conde",
                 "Vila Nova de Gaia",
                 "Abrantes",
                 "Alcanena",
                 "Almeirim",
                 "Alpiar??a",
                 "Benavente",
                 "Cartaxo",
                 "Chamusca",
                 "Const??ncia",
                 "Coruche",
                 "Entroncamento",
                 "Ferreira do Z??zere",
                 "Goleg??",
                 "Ma????o",
                 "Our??m",
                 "Rio Maior",
                 "Salvaterra de Magos",
                 "Santar??m",
                 "Sardoal",
                 "Tomar",
                 "Torres Novas",
                 "Vila Nova da Barquinha",
                 "Alc??cer do Sal",
                 "Alcochete",
                 "Almada",
                 "Barreiro",
                 "Gr??ndola",
                 "Moita",
                 "Montijo",
                 "Palmela",
                 "Santiago do Cac??m",
                 "Seixal",
                 "Sesimbra ",
                 "Set??bal",
                 "Sines",
                 "Arcos de Valdevez",
                 "Caminha",
                 "Melga??o",
                 "Mon????o",
                 "Paredes de Coura",
                 "Ponte da Barca",
                 "Ponte de Lima",
                 "Valen??a",
                 "Viana do Castelo",
                 "Vila Nova de Cerveira",
                 "Alij??",
                 "Boticas",
                 "Chaves",
                 "Mes??o Frio",
                 "Mondim de Basto",
                 "Montalegre",
                 "Mur??a",
                 "Peso da R??gua",
                 "Ribeira de Pena",
                 "Sabrosa",
                 "Santa Marta de Penagui??o",
                 "Valpa??os",
                 "Vila Pouca de Aguiar",
                 "Vila Real",
                 "Armamar",
                 "Carregal do Sal",
                 "Castro Daire",
                 "Cinf??es",
                 "Lamego",
                 "Mangualde",
                 "Moimenta da Beira",
                 "Mort??gua",
                 "Nelas",
                 "Oliveira de Frades",
                 "Penalva do Castelo",
                 "Penedono",
                 "Resende",
                 "Santa Comba D??o",
                 "S??o Jo??o da Pesqueira",
                 "S??o Pedro do Sul",
                 "S??t??o",
                 "Sernancelhe",
                 "Tabua??o",
                 "Tarouca",
                 "Tondela",
                 "Vila Nova de Paiva",
                 "Viseu",
                 "Vouzela",
                 "Angra do Hero??smo",
                 "Calheta",
                 "Horta",
                 "Lagoa",
                 "Lajes das Flores",
                 "Lajes do Pico",
                 "Madalena",
                 "Nordeste",
                 "Ponta Delgada",
                 "Povoa????o",
                 "Praia da Vit??ria",
                 "Ribeira Grande",
                 "Santa Cruz da Graciosa",
                 "Santa Cruz das Flores",
                 "S??o Roque do Pico",
                 "Velas",
                 "Vila do Corvo",
                 "Vila do Porto",
                 "Vila Franca do Campo",
                 "Calheta",
                 "C??mara de Lobos",
                 "Ponta do Sol",
                 "Porto Moniz",
                 "Ribeira Brava",
                 "Funchal",
                 "Machico",
                 "Porto Santo",
                 "Santa Cruz",
                 "Santana",
                 "S??o Vicente"
                ];
/*initiate the autocomplete function on the "myInput" element, and pass along the countries array as possible autocomplete values:*/
autocomplete(document.getElementById("myInput"), concelhos);
