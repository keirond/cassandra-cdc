#!/bin/bash
# shellcheck disable=SC2001,SC2034,SC2155

export NAMESPACE="ndl"
export IMAGE="nexus.fnc.reg/vta/cassandra-cdc:08.01.2025.01"
export STATEFUL_SET_NAME="ndl-cassandra"
export VOLUME_PREFIX="data-${STATEFUL_SET_NAME}"

# Get PVCs related to the StatefulSet
PVC_LIST=$(kubectl get pvc -n $NAMESPACE -o name | grep $VOLUME_PREFIX)

# Deploy a CDC pod for each PVC
for PVC in $PVC_LIST; do
  export PVC_NAME="data-ndl-cassandra-1"
  export VOLUME_INDEX=$(echo "$PVC_NAME" | sed "s/${VOLUME_PREFIX}-//")

  envsubst < deployment.yml | kubectl delete -f -
  envsubst < deployment.yml | kubectl apply -f -
  #envsubst < deployment.yml > deployment-"${VOLUME_INDEX}".yml
done
