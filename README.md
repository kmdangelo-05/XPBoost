# ✦ XPBoost

Plugin **Paper/Spigot** per assegnare booster XP ai giocatori, con persistenza su MySQL, GUI interattiva e notifiche completamente configurabili.

---

## Funzionalità

- **Booster Multiplier Stacking** — ogni giocatore tanti booster che accumulano i loro moltiplicatore
- **Online/Offline booster mode** — i booster possono scorrere solo mentre il giocatore è connesso oppure anche mentre è disconnesso
- **Moltiplicatore cumulativo** — i moltiplicatori si sommano tra loro
- **GUI interattiva** — lista booster, rimozione e preferenze tramite inventario in-game con paginazione
- **Action bar** — mostra il moltiplicatore totale attivo e il tempo rimanente in tempo reale
- **Notifica XP** — messaggio in chat quando si raccoglie XP con il booster attivo (con delay configurabile per non spammare)
- **Persistenza MySQL** — tutti i dati salvati su database tramite HikariCP connection pool
- **AutoSave asincrono** — salvataggio automatico a intervalli configurabili senza bloccare il main thread
- **Salvataggio al logout** — i dati vengono persistiti quando il giocatore esce e ogni x secondi (configurabili)

---

## Requisiti

| Componente | Versione minima |
|---|---|
| Paper / Spigot | 1.21.4 |
| Java | 21 |
| MySQL / MariaDB | qualsiasi versione recente |

---

## Installazione

1. Scarica il `.jar` dalla pagina [Releases](../../releases)
2. Inseriscilo nella cartella `plugins/` del server
3. Avvia il server — verranno generati `config.yml` e `messages.yml`
4. Configura la connessione al database in `config.yml`
5. Riavvia il server

---

## Configurazione

### config.yml

```yaml
database:
  url: 'jdbc:mysql://localhost:3306/XPBoost'
  username: root
  password: ""
  update_every_seconds: 300           # secondi tra un autosave e il successivo
  update_delay_from_server_start: 10  # secondi di attesa prima del primo autosave

settings:
  global_xp_multiplier: 1          # moltiplicatore globale applicato a tutti i giocatori indiscriminatamente (non appare nelle notifiche del plugin)
  xp_message_delay: 5              # secondi minimi tra un messaggio XP e il successivo

  booster_gui_appearance:
    booster_item_title: "&aBooster %booster_number%"
    booster_item_lore:
      - "&bTempo d'acquisto: %starting_time%"
      - "&bTempo rimanente: %remaining_time%"
      - "&eMoltiplicatore: %multiplier%"
    booster_item_lore_online_only_true: "&6Attivo solo online: &a&lVERO"
    booster_item_lore_online_only_false: "&6Attivo solo online: &4&lFALSO"
    booster_item_lore_remotion_guide: "&4&lTASTO SINISTRO PER RIMUOVERE IL BOOSTER"
    remotion_gui_title: "&4&lRimuovi booster"
    list_gui_title: "&3&lI tuoi booster"
    preferences_gui_title: "&3&lLe tue preferenze"
```

#### Placeholder — booster_item_lore

| Placeholder | Descrizione |
|---|---|
| `%booster_number%` | Numero progressivo del booster nella lista |
| `%starting_time%` | Durata originale del booster (formattata, es. `1h 30m 0s`) |
| `%remaining_time%` | Tempo rimanente del booster (formattato) |
| `%multiplier%` | Moltiplicatore del booster (es. `1.5`) |

---

### messages.yml

```yaml
Lang:
  prefix: ""
  action_bar: "&l&2XP BOOST %multiplier%X: &b%remaining_time%&f"
  xp_chat_message: "&3&lHai ottenuto %new_xp% xp invece di %old_xp% xp!&f&r"
  expired_booster_message: "&cIl tuo booster &e%multiplier%x &cè scaduto!&f"
  successfully_added_booster_message: "Il tuo booster %multiplier% di %duration% è stato aggiunto!"
  successfully_added_booster_message_with_receiver_name: "&3Booster &e%multiplier% &3di %duration% aggiunto a %receiver_name%!"
  successfully_removed_booster_message: "&3Il tuo booster &e%multiplier% &3è stato rimosso!"
  successfully_removed_booster_message_with_receiver_name: "Booster %multiplier% rimosso da %receiver_name%!"
```

#### Placeholder — messages.yml

| Chiave | Placeholder disponibili |
|---|---|
| `action_bar` | `%multiplier%`, `%remaining_time%` |
| `xp_chat_message` | `%old_xp%`, `%new_xp%` |
| `expired_booster_message` | `%multiplier%` |
| `successfully_added_booster_message` | `%multiplier%`, `%duration%` |
| `successfully_added_booster_message_with_receiver_name` | `%multiplier%`, `%duration%`, `%receiver_name%` |
| `successfully_removed_booster_message` | `%multiplier%` |
| `successfully_removed_booster_message_with_receiver_name` | `%multiplier%`, `%receiver_name%` |

> I colori si scrivono con `&` seguito dal codice colore (es. `&a` verde, `&c` rosso, `&e` giallo). Se `action_bar` è vuoto, la barra non viene mostrata.

---

## Comandi

| Comando | Descrizione |
|---|---|
| `/xpboost add <mult> <durata> [giocatore] [online_only]` | Aggiunge un booster in coda |
| `/xpboost remove [giocatore]` | Apre la GUI per rimuovere un booster |
| `/xpboost reset [confirm] [giocatore]` | Azzera tutti i booster (richiede `confirm`) |
| `/xpboost list [giocatore]` | Mostra tutti i booster attivi e in coda |
| `/xpboost preferences [action_bar\|xp_chat_message\|toggle_boosters] [true\|false]` | Gestisce notifiche e attivazione booster |
| `/xpboost help` | Mostra la lista dei comandi |

`<obbligatorio>` — `[opzionale]`

### Note sui comandi

**`/xpboost add`** — `online_only` è un parametro booleano opzionale (`true`/`false`). Se `true`, il booster si consuma solo mentre il giocatore è online, congelando la durata durante i periodi di disconnessione.

**`/xpboost preferences toggle_boosters`** — attiva o disattiva tutti i booster del giocatore senza rimuoverli. La durata si congela finché non vengono riattivati.

**`/xpboost preferences action_bar`** — mostra o nasconde la action bar con il moltiplicatore attivo.

**`/xpboost preferences xp_chat_message`** — mostra o nasconde il messaggio in chat alla raccolta XP.

**`/xpboost reset`** — richiede di scrivere `/xpboost reset confirm` per evitare reset accidentali.

---

## Database

Le tabelle vengono create automaticamente all'avvio:

```sql
-- Associazione giocatore <-> booster
player_boosters (player_uuid, booster_uuid)

-- Dati del singolo booster
boosters (booster_uuid, multiplier, remaining_duration,
          final_time_millis, starting_duration_millis, online_only)

-- Preferenze notifiche del giocatore
preferences (player_uuid, action_bar, xp_chat_message)
```

---

## Stack tecnico

| Libreria | Versione |
|---|---|
| HikariCP | 5.1.0 |
| mysql-connector-j | 8.3.0 |
| Lombok | 1.18.30 |
| Gradle Shadow Plugin | 8.1.1 |

---

## Licenza

Distribuito sotto licenza [MIT](LICENSE).
