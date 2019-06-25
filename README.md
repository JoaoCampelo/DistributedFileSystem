# DistributedFileSystem

Este projecto propõe o desenvolvimento de um sistema de partilha e sincronização de ficheiros semelhante ao Sistema Dropbox (sem interface web) de acordo com os requisitos abaixo enumerados:

  •O serviço deverá permitir partilhar/replicar uma pasta remota (e todas as sub-pastas e ficheiros dentro dessa pasta). Cada  cliente terá uma réplica local da pasta que será sincronizada automaticamente com as outras réplicas existentes noutras máquinas; (FEITO)
  
  •Para acederem ao serviço os clientes devem efectuar um registo. Nesse registo escolhem o username, a password e o nome do group a que querem pertencer, podem também criar um novo grupo ao qual se pretendem associar; (FEITO)
  
  •Posteriormente, os clientes efectuam a autenticação e acedem à área de trabalho/pasta do grupo em que se registaram. Nessa  área/pasta poderão efectuar operações básicas de gestão de pastas (e.g.list, create, delete, rename, move, etc.) e ficheiros (e.g.upload, delete, rename, move, etc.); (FEITO)
  
  •Depois de autenticados os clientes devem trocar informação entre os pares do seu grupo de modo a manterem a sincronização dos recursos (cf. pastas e ficheiros). O sistema deverá gerir automaticamente os acessos simultâneos de diferentes clientes aos mesmos  recursos, ou seja, ordenar/seriaros acessos aos recursosde forma exclusiva;
  
  •O sistema deverá ainda permitir gerir a propagação das actualizações, ou seja, sempre que existir uma operação de actualização num dos recursos (cf. pastas ou ficheiros), todos os pares devem ficar com a mesma versão; (FEITO)
  
  •O sistema deve ainda garantir a tolerância a falhas, ou seja, sempre que um dos nós falhar os restantes devem garantir a continuidade do serviço;
  
  •Deverão ainda ser consideradas questões de segurança tanto na autenticação como na partilha de conteúdos de modo controlar acessos indevidos aos recursos partilhados.
  
 Para a realização deste projecto foi utilizado RMI para garantir as conexões entre clientes e servidores, e os desing patterns Factory e Observer, no caso do primeiro para criar sessões para os clientes, e o segundo para espalhar as alterações efetuadas nas pastas para todos os clientes que tem permissão para aceder a essa mesma pasta.
