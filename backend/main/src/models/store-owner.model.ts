import {model, property, belongsTo, Entity} from '@loopback/repository';
import {Account, AccountWithRelations} from '.';

@model({settings: {strict: false}})
export class StoreOwner extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true
  })
  id?: number;

  @property({
    type: 'string',
  })
  picture?: string;


  @property({
    type: 'string',
  })
  country?: string;

  @property({
    type: 'string',
  })
  city?: string;

  @property({
    type: 'string',
  })
  postal_code?: string;

  @property({
    type: 'string',
  })
  street_name?: string;
  @property({
    type: 'string',
  })
  street_number?: string;

  
  @belongsTo(() => Account, {name: 'account', keyTo: 'email'})
  account_email: string;

  // Define well-known properties here

  // Indexer property to allow additional data
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [prop: string]: any;

  constructor(data?: Partial<StoreOwner>) {
    super(data);
  }
}

export interface StoreOwnerRelations {
  account?: AccountWithRelations;
}

export type StoreOwnerWithRelations = StoreOwner & StoreOwnerRelations;
