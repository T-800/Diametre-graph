#!/bin/bash

######

# Script donné à titre indicatif - nous n'assurons pas la hotline...

# Pour utiliser ce script, créer un répertoire rep dans lequel mettre tous les fichiers .pfg ainsi que ce script.
# Puis créer un sous-répertoire de rep dans lequel mettre votre programme .c, .java, .py ou .tar
# Usage : ./correc_etudiant.sh fichier_note fichier_erreur

# $1 = fichier de notes ; $2 = fichier de log/erreurs

######

# pour éviter l'erreur "unmappable character for encoding ASCII" avec Java
# quand le fichier contient des caractères bizarres
export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8

# en remplacement de la commande bash timeout si celle-ci n'est pas disponible
function timeout() { perl -e 'alarm shift; exec @ARGV' "$@"; }

for rep in *
do
    if [[ -d $rep ]] # si c'est un répertoire
    then
	echo "$rep" >> $1 # le nom du répertoire
	echo "$rep" >> $2
	echo "$rep"
	cd $rep

	shopt -s nullglob # pour éviter une erreur dans les boucles suivantes
			  # s'il n'y a pas de fichier

	# si fichier Java direct, il faut le renommer en Main.java
	for fic in Soumission*.java
	do
	    mv $fic Main.java 2>> ../$2
	done

	for fic in *.tar # on détare si nécessaire
	do
	    tar -xf $fic 2>> ../$2
	done

	# argument du programme : chemin des fichiers de test
	param=("../arbre.pfg"
	    "../chemin5.pfg"
	    "../chemin1000.pfg"
	    "../clique5.pfg"
	    "../clique20.pfg"
	    "../cycle5.pfg"
	    "../cycle7.pfg"
	    "../cycle500.pfg"
	    "../graphe5.pfg"
	    "../graphe100.pfg"
	    "../graphe500.pfg"
	    "../graphe700.pfg"
	    "../graphe10000.pfg"
	    "../graphe20000.pfg"
	    "../graphe100000.pfg"
	    "../graphe1000000.pfg")


	# type de fichier (python, java ou C) et ligne de commande pour exécuter
	for fic in *Main*.py
	do
	    exe="python3 $fic"
	done

	for fic in *main_python2*.py
	do
	    exe="python2.7 $fic"
	done

	for fic in *main*.c
	do
	    gcc *main*.c -o main 2>> ../$2
	    exe="./main"
	done

	if [[ -f Main.java ]]
	then
	    javac *.java 2>> ../$2
	    exe="java Main"
	fi

	# résultats attendus : exact et heuristique
	tests=(4 4
	    4 4
	    999 999
	    1 1
	    1 1
	    2 2
	    3 3
	    250 250
	    3 3
	    7 7
	    7 7
	    8 7
	    8 7
	    12 11
	    6 5
	    x 6)
	note=0 # pour connaître la note finale
	for ((i = 0 ; 16 - $i ; i++))
	do
	    if [[ $i -le 13 ]] # ne pas lancer l'algo exact sur les deux derniers graphes
	    then
		echo "$i: $exe e ${param[$i]}"
		res=`(time timeout 60 $exe e ${param[$i]}) 2>> ../$2` # exécution avec option e
		res=$(echo -e $res | tr -d ' \n') # nettoyage de la sortie
		echo $res >> ../$2
		if [[ $res -eq ${tests[2*$i]} ]] # test de validité pour l'option e
		then
		    note=$(($note + 1)) # si c'est bon, on ajoute un point
		else
		    echo "resultat faux ($res) : $exe e ${param[$i]}" >> ../$2
		fi
	    fi
	    echo "$i: $exe h ${param[$i]}"
	    res=`(time timeout 60 $exe h ${param[$i]}) 2>> ../$2` # exécution avec option h
	    res=$(echo -e $res | tr -d ' \n') # nettoyage de la sortie
	    echo $res >> ../$2
	    if [[ $res -eq ${tests[2*$i+1]} ]] # test de validité pour l'option h
	    then
		note=$(($note + 1)) # si c'est bon, on ajoute un point
	    else
		echo "resultat faux ($res) : $exe h ${param[$i]}" >> ../$2
	    fi
	done
	
	echo "$note (sur 30)" >> ../$1 # affichage de la note
	echo "********" >> ../$1 # séparateur
	echo "********" >> ../$2
	cd .. # revenir dans le répertoire de base
    fi
done
