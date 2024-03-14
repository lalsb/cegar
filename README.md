CEGAR
===



Beschreibung
---

Dieses Repository beinhaltet eine einfache Implementierung von CEGAR (Clarke et al., 2023).

Es folgen eine Installationsanleitung und eine Einführung in die Bedienung.

The related documentation for each sample can be found [here](https://openjfx.io/openjfx-docs/).

For more information go to https://openjfx.io.



Inhalt
---

* [Installationsanleitung](#installation)
* [Einführung in die Bedienung](#instructions)
* [Beispiele](#examples)

Installationsanleitung<a name="installation" />
---

Für die Ausführung wird eine aktuelle OpenJDK benötigt, dieses Repository
beinhaltet allerdings bereits die OpenJDK `jdk-21.0.2` für Windows und ist für diese
konfiguriert. Für andere Betriebssysteme kann die aktuelle Version der JDK [hier](https://jdk.java.net/21/)
heruntergeladen werden. 

Um eine andere als die vorkonfigurierte OpenJDK zu verwenden, kann dann `gradle.properties`
wie folgt bearbeitet werden.

`org.gradle.java.home=C:/my/path/to/jdk-21.0.2`

Der enthaltene `gradle-wrapper` lädt eine aktuelle gradle-version automatisch bei
der ersten Ausführung nach. Ist bereits eine `gradle`-Installation verhanden,
wird dies i. d. R erkannt.

Einführung in die Bedienung<a name="instructions" />
---

### Eingabe

Die Applikation öffnet sich im Tab Transitions. Hier können Transitionsblöcke 
als Repräsentation einer Kripke-Struktur mit

- `Name` als Name einer Variable,
- `Intials` als Startwerte,
- `Domain` als Wertebereich

eingetragen werden. Darauf folgen beliebig viele Transitionen, wobei mit dem
Button `+` eine neue Zeile hinzugefügt werden kann und bestehende Zeilen mit 
dem Button `-` entfernt werden können. Eine Transition besteht aus

- `Condition` als Bedindung für die Transition,
- `Action` als ein neuer Werte, oder eine Mengen an neuen Werten.

In das Feld `Name` sollten Literale, für `Intials` und `Domain` mit Kommata 
separierte Werte eingetragen werden. In `Condition` können Prädikate und in
`Action` arithmetische Ausdrücke eingetragen werden. Dabei kann können die
Tastatur oder die Buttons für boolesche Operatoren und Relationen verwendet
werden. Wahrheitswerte werden als 0 (falsch) und 1 (wahr) eingetragen.

*Besonderheit:* In `Action` können mehrere arithmetische Ausdrücke eingetragen
werden, indem zunächst ein Ausdruck eingetragen und dieser mit `ENTER` bestätigt
wird. Das Feld `Action` wir durch diesen Befehl aufgeteilt, sodass ein neuer
Ausdruck eingetragen werden kann. Jeder Ausdruck (auch der Erste, falls nur ein
Ausdruck benötigt wird, muss mit `ENTER` bestätigt werden). Unerwünschte
Ausdrücke können mit dem jeweiliegen Button `-` entfernt werden.

Das Feld `Else` kann wie ein Feld `Action` behandelt werden. 

Ein Transitionsblock kann mit dem Button `Add` hinzugefügt werden. Das Formular
leert sich und die Eingabe erscheint in der darüberliegenden Tabelle. Nun kann 
ein neuer Transitionsblock eingetragen werden. Ein bestehender Tabelleneintrag
kann mittels linker Maustaste angewählt und in das Formular geladen werden. Eine
Änderung kann mittels `Apply` gesichert werden. *Achtung:* Hier funktioniert nur
`Apply` - `Add` fügt immer einen neuen Transitionsblock hinzu (ggf. Duplikate).
Der Button `Delete` löscht den ausgewählten Tabelleneintrag, `Clear` leert nur
die Felder des Formulars für eine neue Eingabe. 

Alle Tabelleneinträge können mittels `Load` und `Save` serialisiert und
deserialisiert werden.

### Ausführung

Der konkrete Graph kann mittels `Generate Original Graph` visualisiert werden.
Die Applikation wechselt dann in den Tab Graph. Mittels `Generate Initial Abstraction`
kann die initiale Abstraktion visualisiert werden. 
*Achtung:* Es wird immer nur ein Graph angezeigt. Eine neue Visualisierung überschreibt
voherige Visualisierungen.

Die Visualisierung kann folgendermaßen untersucht werden.

- `Verschiebung der Ebene` durch Halten der rechten Maustaste und Ziehen.
- `Verschiebung eines Knotens` durch Halten der linken Maustaste und Ziehen.
- `Zoomen` durch Scrollen des Mausrades.
- `Umschalten des automatischen Layout` mit dem Button `Automatic Layout`.
- `Umschalten der Label-Modi` mit dem Menü `Dispaly Label`.

Da kein Model-Checker angebunden ist, können Gegenbeispiele manuell eingetragen
werden, wobei endliche Pfade in `finite Part` eingetragen werden. Dabei wird
eine mit Kommata separierte Folge von Zustands-Ids ohne Leerzeichen erwartet.
Unendliche Pfade mit Schleife werden so eingetragen, dass der endliche Teil
in `finite Part` und der unendliche Teil in `looping Part` eingetragen wird.

Der Pfad muss dann zunächst mit `Check Path` geprüft werden und anschließend
kann mit `Refine abstraction` eine Verfeinerung generiert werden.


Beispiele<a name="examples" />
---

### SquentialIncrement-Beispiel aus Clarke et al., S. 760

Das Beispiel kann mittels `Load -> SquentialIncrement.ser` geladen werden.

Mögliche fehlerhafte Gegenbeispiele sind:

- 1,4,5,0,4,1,6
- 1,6,4,7
- 1,4,5,2
- 1 && 4,5,0

### IllegalInpit-Beispiel (eigenes Beispiel)

Das Beispiel kann mittels `Load -> IllegalInput.ser` geladen werden.
Es ist syntaktisch richtig, hat aber einen semantischen Fehler. So
kann die Fehlerbehandlung mittels `ModelInputException` gezeigt werden.


### Elevator-Beispiel (eigenes Beispiel)

Das Beispiel kann mittels `Load -> elevator.ser` geladen werden. Es
veranschaulicht das Fahrstuhlsystem aus Beispiel 2 (Dort wurde die
manuelle Abstraktion manuell berechnet).

Die Implementierung verwendet die Bibliothek [Smartgraph](https://github.com/brunomnsilva/JavaFXSmartGraph) für die Visualisierung 
und [mXParser](https://mathparser.org/mxparser-license/) für die Auswertung von Ausdrücken, sowie [AtlantaFX](https://mkpaz.github.io/atlantafx/) für die UI.




