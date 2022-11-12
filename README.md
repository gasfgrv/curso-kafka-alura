# Apache Kafka

## Rodando o kafka localmente:
    
**Inicializando**

- `bin/zookeeper-server-start.sh config/zookeeper.properties`
- `bin/kafka-server-start.sh config/server.properties`

O kafka precisa do zookepper para salvar algumas informações básicas

Tentar rodar os comandos na pasta raiz do kafka

A porta padrão do kafka é a 9092

**Criando um tópico**

- `bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic nome_topico`

Não se recomenda utlizar "." e "_" ao mesmo tempo ao nomear um tópico

**Listando os tópicos**

- `bin/kafka-topics.sh --list --bootstrap-server localhost:9092`

A flag --describe mostra todos os detalhes de cada tópico

**Produzindo mensagens**

- `bin/kafka-console-producer.sh --broker-list localhost:9092 --topic nome_topico`

Ao executar esse comando, cada linha de comando será uma mensagem produzida

- Consumindo mensagens
        
- `bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic nome_topic`

Esse comando consome as mensagens que vem após a sua execução

Para ler todas as mensagens desde o inicio, basta adicionar a flag --from-beginning

**Alterando um tópico já existente**
        
- `bin/kafka-topics.sh --alter --bootstrap-server localhost:9092 --topic topic_name --partitions <quantidade>`

**Listando todos os grupos**

- `bin/kafka-consumer-groups.sh --all-groups --bootstrap-server localhost:9092 --describe`


## Paralelizando e a Importancia das keys

Para que consumidores diferentes de um tópico consigam ler as mensagem, eles são separados em grupos diferentes.

Por padrão cada tópico do kafka só possui apenas uma partição.

A quantidade de consumers em paralelo (no mesmo grupo) depende da quantidade de partições do tópico.

Caso a quantidade de consumers for inferior que a de partições, o kafka faz o rebalacemento para que cada consumer seja responsável por uma ou mais partições.

Caso o numero de consumers for maior, um consumer ficará sem ler as mensagens.

O kafka usa as chaves das mensagens para definir qual será o consumer que vai 
ler as chaves.

O kafka atua em paralelo entre chaves diferentes, mas sequencial para a mesma chave.

Qual a importância das chaves na paralelização de tarefas?

Ela é peça fundamental para paralelizar o processamento de mensagens em um tópico dentro do mesmo consumer group.

A chave é usada para distribuir a mensagem entre as partições existentes e consequentemente entre as instâncias de um serviço dentro de um consumer group.

## Serialização Customizada

Por padrão o kafka salva  os aquivos de logs (registros) em pastas temporárias do SO (/tmp), para evitar de perder os dados, deve-se alterar os arquivos config/server.properties e zookeeper.properties apontando para o local onde esses dados serão salvos.

## Fast delegate

Quanto menos código em toda a ponta de entrada, mais rápido você delega para o sistema de mensagens, quanto mais rápido você enviar essa mensagem menos chance de dar erro

## Single point of failure

Caso o broker do kafka, caia e não esteja em um cluster, não é possivel haver a troca de mensagens

## Broker

O conceito de broker na plataforma do Kafka é nada mais do que praticamente o proprio Kafka, ele é quem gerencia os tópicos, define a forma de armazenamento das mensagens, logs etc.

O Broker é o coração do ecossistema do Kafka. Um Kafka Broker é executado em uma única instância em sua máquina. Um conjunto de Brokers entre diversas máquinas formam um Kafka Cluster.

## Cluster

O conceito de cluster é nada mais do que um conjunto de Brokers que se comunicam entre si ou não para uma melhor escalabilidade e tolerância a falhas.

Uma das principais características do Kafka é a escalabilidade e resiliência que ele oferece. Você pode rodar o Kafka local na sua máquina onde sua própria máquina teria um Kafka Broker formando um Kafka Cluster, como pode subir n instâncias de Kafka Brokers e todas estarem no mesmo Kafka Cluster. Com isso é possível escalar sua aplicação, e replicar os dados entre os Brokers.

## Replicação em cluster (Local)

- Duplicar o arquivo config/server.properties
- Mudar o valor de broker.id
- Mudar o valor de log.dirs
- Mudar a propriedade listeners para definir a porta do kafka
- Adicionar a propriedade default.replication.factor para que cada tópico tenha cópias nos demais brokers
- Mudar o valor de offset.topics.replication.factor
- Mudar o valor de transaction.state.log.replication.factor
- O kafka recomenda que o valor de replicação seja 3

## Acks
Denota o número de corretores que devem receber o registro antes de considerarmos a gravação como bem-sucedida.

Ele suporta três valores — 0, 1, e all

- 0: Sempre considera como bem sucedido
- 1: Considera como bem sucedido quando o lider confirma o recebimento
- all: Considera como bem sucedido quando o lider e as replicas confirmam o recebimento
