Progetto per l'esame di ingegneria del software. Anno 2019/2020

<h1>JustMeet</h1>

Si vuole realizzare un sistema software che permetta l’incontro tra persone al fine di svolgere attività cooperative e di squadra.
L'applicativo permette la gestione di eventi tramite la creazione di nuovi, modifica, annullamento e la partecipazione.
Gli utenti possono registrarsi nell'applicativo utilizzando un'email e password, una volta aver effettuato l'accesso, possono visualizzare tutti gli eventi presenti oppure crearne di nuovi.
Una volta effettuata la partecipazione ad un evento l'utente può, al termine dell'evento, rilasciare una recensione agli utenti che vi hanno partecipato, questo sarà possibile una volta che l'utente avrà visualizzato il profilo di un altro utente partecipante.
Ciascun evento ha un numero minimo di partecipanti il quale, se non verrà raggiunto entro la mezzanotte del giorno precedente, verrà considerato come annullato.
Gli utenti vengono notificati dal sistema nei casi in cui: 
<ul>
 <li>
  Un evento viene confermato al raggiungimento del numero minimo di partecipanti
 </li>
 <li>
  Un evento viene modificato dall'organizzatore
 </li>
 <li>
  Un evento viene annullato dall'organizzatore
</ul>

Nello sviluppo dell’applicazione sarà necessario focalizzarsi e definire la lista degli eventi che la piattaforma supporta.

<h2>Utilizzo:</h2>
<h3>Avvio backend</h3>
<code>gradlew bootRun</code>
<br><br>Alternativamente avviare l'eseguibile all'interno della cartella build/libs tramite:
<br><code>java -jar justmeet-0.0.1-SNAPSHOT.jar</code> 
<br><h3>Avvio Frontend </h3>
<code>gradlew run</code> 
<br>Alternativamente avviare l'eseguibile all'interno della cartella Frontend/out/artifacts/Frontend_jar tramite:
<code>java -jar justmeet.jar</code>
<br><br> A questo punto sarà possibile utilizzare il programma.

<h2>Generazione eseguibile</h2>
<h3>Backend</h3>
<code>gradlew build</code>
<br><br>L'eseguibile si troverà all'interno della cartella <code>backend/build/libs/justmeet-0.0.1-SNAPSHOT.jar</code>
<h3>Frontend</h3>
<code>gradlew bootJar</code>
<br><br>L'eseguibile si troverà nella cartella <code>Frontend/out/artifacts/Frontend_jar/Frontend.jar</code>
<br><br>PS: Prima di avviare il backend è necessario avere implementato un database, per fare questo si può importare la struttura del database inserita nel path justmeet/backend/justmeet.sql
