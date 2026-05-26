# XPBoost

Plugin Spigot per la gestione di booster XP personalizzati per giocatore, con persistenza su database MySQL.

---

## Requisiti

- Paper/Spigot 1.21+
- Java 21+
- MySQL / MariaDB (es. XAMPP)

---

## Installazione

1. Copia `XPBoost.jar` nella cartella `plugins/` del tuo server.
2. Avvia il server una volta per generare i file di configurazione.
3. Configura `plugins/XPBoost/config.yml` con i dati del tuo database MySQL.
4. Riavvia il server.

---

## Configurazione

### config.yml

```yaml
database:
  url: 'jdbc:mysql://localhost:3306/XPBoost'
  username: root
  password: ""
  character_encoding: utf8
  use_ssl: "false"
  update_every_seconds: 300
  update_delay_from_server_start: 10

settings:
  global_multiplier: 1
```

### messages.yml

Tutti i messaggi sono personalizzabili tramite `plugins/XPBoost/messages.yml`.
Supporta i codici colore Minecraft con il prefisso `&`.

---

## Comandi

| Comando | Descrizione |
|---|---|
| `/xpboost give <moltiplicatore> <durata> [giocatore]` | Assegna un booster e lo attiva subito |
| `/xpboost add <moltiplicatore> <durata> [giocatore]` | Aggiunge un booster in coda senza attivarlo |
| `/xpboost remove <moltiplicatore> [giocatore]` | Rimuove il booster con quel moltiplicatore |
| `/xpboost reset [confirm] [giocatore]` | Azzera tutti i booster di un giocatore |
| `/xpboost list [giocatore]` | Mostra la lista dei booster attivi e in coda |
| `/xpboost preferences [actionbar\|xpchatmessage] [true\|false]` | Abilita o disabilita le notifiche |
| `/xpboost help` | Mostra la lista dei comandi |

**Parametri:**
- `<moltiplicatore>` — numero decimale, es. `2.0` per il doppio XP
- `<durata>` — durata in secondi, es. `3600` per un'ora
- `[giocatore]` — opzionale, nome del giocatore target

---

## Funzionalità

- Ogni giocatore può avere più booster in coda con moltiplicatori diversi
- Un solo booster può essere attivo alla volta
- I dati vengono salvati su MySQL e persistono anche a server spento
- Salvataggio automatico configurabile ogni N secondi
- Notifiche personalizzabili in chat e in action bar
- Supporto per operazioni asincrone (nessun lag al main thread)

---

## Database

Il plugin crea automaticamente due tabelle al primo avvio:

- `player_boosters` — booster di ogni giocatore con moltiplicatore, durata e stato attivo
- `preferences` — preferenze di notifica di ogni giocatore

---

## Autore

Sviluppato da **kmdangelo-05**  
Repository: https://github.com/kmdangelo-05/XPBoost
